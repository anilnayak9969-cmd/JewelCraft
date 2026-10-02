package com.example.JewelCraft.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.mapping.BasicValuedModelPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

/**
 * Runs first at start-up and fixes two things ddl-auto=update never does, both of which make
 * inserts fail with "This record is linked to other data or already exists":
 * <ol>
 * <li>Another project pointed at this database can leave its own NOT NULL columns in our tables
 * (e.g. a payments.payment_method from a food-delivery app). Any NOT NULL column without a default
 * that no entity maps is made nullable. Nothing is dropped, so no data is lost.</li>
 * <li>Enums are stored in MySQL ENUM(...) columns, and a value added to a Java enum later
 * (e.g. TrackingEvent.STORE_UPDATE) is not added to the column. Missing values are added;
 * existing ones are always kept.</li>
 * </ol>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SchemaRepair implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SchemaRepair.class);
	private static final Pattern ENUM_VALUE = Pattern.compile("'((?:[^']|'')*)'");

	private final EntityManagerFactory entityManagerFactory;
	private final JdbcTemplate jdbc;

	public SchemaRepair(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
		this.entityManagerFactory = entityManagerFactory;
		this.jdbc = new JdbcTemplate(dataSource);
	}

	@Override
	public void run(String... args) {
		try {
			relaxUnmappedRequiredColumns();
		} catch (RuntimeException e) {
			log.warn("Schema check (unused columns) skipped: {}", e.getMessage());
		}
		try {
			addMissingEnumValues();
		} catch (RuntimeException e) {
			log.warn("Schema check (enum values) skipped: {}", e.getMessage());
		}
	}

	private void relaxUnmappedRequiredColumns() {
		Map<String, Set<String>> mapped = mappedColumnsByTable();
		List<Map<String, Object>> blocking = jdbc.queryForList("""
				SELECT table_name AS tbl, column_name AS col, column_type AS type
				FROM information_schema.columns
				WHERE table_schema = DATABASE() AND is_nullable = 'NO'
				  AND column_default IS NULL AND extra = ''""");
		for (Map<String, Object> c : blocking) {
			String table = c.get("tbl").toString();
			String column = c.get("col").toString();
			Set<String> ours = mapped.get(table.toLowerCase(Locale.ROOT));
			if (ours == null || ours.contains(column.toLowerCase(Locale.ROOT))) {
				continue; // not a JewelCraft table, or a column we actually use
			}
			jdbc.execute("ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` " + c.get("type") + " NULL");
			log.warn("Made {}.{} nullable: it is not used by JewelCraft (probably added by another project "
					+ "sharing this database) and was blocking inserts", table, column);
		}
	}

	private void addMissingEnumValues() {
		Map<String, Class<?>> enumColumns = mappedEnumColumns();
		List<Map<String, Object>> columns = jdbc.queryForList("""
				SELECT table_name AS tbl, column_name AS col, column_type AS type,
				       is_nullable AS nullable, column_default AS dflt
				FROM information_schema.columns
				WHERE table_schema = DATABASE() AND data_type = 'enum'""");
		for (Map<String, Object> c : columns) {
			String table = c.get("tbl").toString();
			String column = c.get("col").toString();
			Class<?> enumType = enumColumns.get(key(table, column));
			if (enumType == null) {
				continue;
			}
			Set<String> values = new LinkedHashSet<>();
			Matcher m = ENUM_VALUE.matcher(c.get("type").toString());
			while (m.find()) {
				values.add(m.group(1).replace("''", "'"));
			}
			List<String> missing = new ArrayList<>();
			for (Object constant : enumType.getEnumConstants()) {
				String name = ((Enum<?>) constant).name();
				if (values.add(name)) {
					missing.add(name);
				}
			}
			if (missing.isEmpty()) {
				continue;
			}
			String list = values.stream().map(v -> "'" + v.replace("'", "''") + "'").collect(Collectors.joining(","));
			String nullability = "NO".equals(c.get("nullable")) ? " NOT NULL" : " NULL";
			String dflt = c.get("dflt") == null ? "" : " DEFAULT '" + c.get("dflt").toString().replace("'", "''") + "'";
			jdbc.execute("ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` enum(" + list + ")" + nullability + dflt);
			log.info("Added {} to {}.{}", missing, table, column);
		}
	}

	/** Table name -> column names, for every table an entity maps to. */
	private Map<String, Set<String>> mappedColumnsByTable() {
		Map<String, Set<String>> result = new HashMap<>();
		entityManagerFactory.unwrap(SessionFactoryImplementor.class).getMappingMetamodel()
				.forEachEntityDescriptor(entity -> entity.forEachSelectable((index, selectable) -> {
					if (!selectable.isFormula()) {
						result.computeIfAbsent(clean(selectable.getContainingTableExpression()), t -> new HashSet<>())
								.add(clean(selectable.getSelectionExpression()));
					}
				}));
		return result;
	}

	/** "table.column" -> Java enum type, for every enum attribute an entity maps. */
	private Map<String, Class<?>> mappedEnumColumns() {
		Map<String, Class<?>> result = new HashMap<>();
		entityManagerFactory.unwrap(SessionFactoryImplementor.class).getMappingMetamodel()
				.forEachEntityDescriptor(entity -> entity.forEachAttributeMapping(attribute -> {
					if (attribute instanceof BasicValuedModelPart basic && !basic.isFormula()
							&& basic.getJavaType().getJavaTypeClass().isEnum()) {
						result.put(key(basic.getContainingTableExpression(), basic.getSelectionExpression()),
								basic.getJavaType().getJavaTypeClass());
					}
				}));
		return result;
	}

	private static String key(String table, String column) {
		return clean(table) + "." + clean(column);
	}

	private static String clean(String name) {
		return name.replace("`", "").toLowerCase(Locale.ROOT);
	}
}
