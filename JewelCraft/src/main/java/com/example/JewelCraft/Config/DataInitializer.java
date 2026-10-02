package com.example.JewelCraft.Config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Entites.Category;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.MetalRate;
import com.example.JewelCraft.Entites.Offer;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.Role;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Entites.DeliveryPartnerProfile;
import com.example.JewelCraft.Entites.StaffProfile;
import com.example.JewelCraft.Enum.Department;
import com.example.JewelCraft.Enum.MetalType;
import com.example.JewelCraft.Enum.VehicleType;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Repositories.CategoryRepository;
import com.example.JewelCraft.Repositories.MetalRateRepository;
import com.example.JewelCraft.Repositories.OfferRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.RoleRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Repositories.StaffProfileRepository;
import com.example.JewelCraft.Repositories.DeliveryPartnerProfileRepository;
import com.example.JewelCraft.Services.OrderTrackingService;
import com.example.JewelCraft.Services.PricingService;

import lombok.RequiredArgsConstructor;

/**
 * Runs once at start-up. Roles are always created. With app.seed.demo-data=true an empty
 * database also gets one login per role, categories, metal rates, sample products and a coupon,
 * so the app can be demonstrated straight away. Nothing is overwritten on later starts.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final MetalRateRepository metalRateRepository;
	private final ProductRepository productRepository;
	private final OfferRepository offerRepository;
	private final PasswordEncoder passwordEncoder;
	private final PricingService pricingService;
	private final OrderTrackingService trackingService;
	private final StaffProfileRepository staffProfileRepository;
	private final DeliveryPartnerProfileRepository deliveryProfileRepository;

	@Value("${app.seed.demo-data}")
	private boolean seedDemoData;

	private final Map<String, Category> categoriesByName = new HashMap<>();

	@Override
	@Transactional
	public void run(String... args) {
		for (RoleName name : RoleName.values()) {
			if (roleRepository.findByName(name).isEmpty()) {
				roleRepository.save(new Role(name));
			}
		}
		int backfilled = trackingService.backfillMissing();
		if (backfilled > 0) {
			log.info("Created tracking history for {} existing orders", backfilled);
		}
		if (!seedDemoData) {
			return;
		}
		if (userRepository.count() == 0) {
			seedUsers();
		}
		if (metalRateRepository.count() == 0) {
			seedMetalRates();
		}
		if (categoryRepository.count() == 0) {
			seedCategories();
		}
		if (productRepository.count() == 0) {
			seedProducts();
		}
		if (offerRepository.count() == 0) {
			seedOffers();
		}
		seedDemoProfiles();
	}

	private void seedUsers() {
		user("Admin", "User", "admin@jewelcraft.com", "Admin@123", "9876500001", RoleName.ADMIN);
		user("Meera", "Kapoor", "manager@jewelcraft.com", "Manager@123", "9876500002", RoleName.STORE_MANAGER);
		user("Rahul", "Verma", "staff@jewelcraft.com", "Staff@123", "9876500003", RoleName.STAFF);
		user("Arjun", "Singh", "delivery@jewelcraft.com", "Delivery@123", "9876500004", RoleName.DELIVERY_PARTNER);
		user("Priya", "Sharma", "customer@jewelcraft.com", "Customer@123", "9876500005", RoleName.CUSTOMER);
		log.info("Demo users created: admin@ / manager@ / staff@ / delivery@ / customer@jewelcraft.com "
				+ "(passwords: Admin@123, Manager@123, Staff@123, Delivery@123, Customer@123). Change them before going live.");
	}

	/** Sample role details for the demo staff accounts (only these demo emails, only if missing). */
	private void seedDemoProfiles() {
		userRepository.findByEmailIgnoreCase("manager@jewelcraft.com")
				.filter(u -> staffProfileRepository.findByUserId(u.getId()).isEmpty())
				.ifPresent(u -> staffProfileRepository.save(StaffProfile.builder().user(u).employeeCode("JC-SM-0001")
						.designation("Store Manager").storeBranch("MG Road, Bengaluru").experienceYears(8)
						.qualification("MBA, Retail Management").previousEmployer("Heritage Jewellers").build()));
		userRepository.findByEmailIgnoreCase("staff@jewelcraft.com")
				.filter(u -> staffProfileRepository.findByUserId(u.getId()).isEmpty())
				.ifPresent(u -> staffProfileRepository.save(StaffProfile.builder().user(u).employeeCode("JC-ST-0001")
						.department(Department.SALES).designation("Jewellery Consultant").storeBranch("MG Road, Bengaluru")
						.experienceYears(3).qualification("B.Com").build()));
		userRepository.findByEmailIgnoreCase("delivery@jewelcraft.com")
				.filter(u -> deliveryProfileRepository.findByUserId(u.getId()).isEmpty())
				.ifPresent(u -> deliveryProfileRepository.save(DeliveryPartnerProfile.builder().user(u)
						.vehicleType(VehicleType.BIKE).vehicleNumber("KA01AB1234").drivingLicenseNumber("KA0120190012345")
						.serviceCity("Bengaluru").servicePincode("560001").build()));
	}

	private void user(String first, String last, String email, String password, String phone, RoleName role) {
		userRepository.save(User.builder()
				.firstName(first)
				.lastName(last)
				.email(email)
				.password(passwordEncoder.encode(password))
				.phone(phone)
				.role(roleRepository.findByName(role).orElseThrow())
				.build());
	}

	/** Starting rates per gram (INR). Store managers update these from Price Management. */
	private void seedMetalRates() {
		rate(MetalType.GOLD, "24K", "7800");
		rate(MetalType.GOLD, "22K", "7150");
		rate(MetalType.GOLD, "18K", "5850");
		rate(MetalType.ROSE_GOLD, "18K", "5850");
		rate(MetalType.WHITE_GOLD, "18K", "5950");
		rate(MetalType.SILVER, "999", "95");
		rate(MetalType.SILVER, "925", "88");
		rate(MetalType.PLATINUM, "950", "3200");
	}

	private void rate(MetalType metal, String purity, String price) {
		metalRateRepository.save(
				MetalRate.builder().metalType(metal).purity(purity).pricePerGram(new BigDecimal(price)).build());
	}

	private void seedCategories() {
		String[][] categories = {
				{ "Rings", "Engagement, cocktail and everyday rings" },
				{ "Necklaces", "Statement necklaces and delicate layers" },
				{ "Earrings", "Studs, drops, jhumkas and hoops" },
				{ "Bracelets", "Chain, tennis and charm bracelets" },
				{ "Bangles", "Traditional and contemporary bangles" },
				{ "Pendants", "Pendants for every occasion" },
				{ "Chains", "Gold, silver and platinum chains" },
				{ "Wedding Jewellery", "Bridal sets and wedding essentials" },
				{ "Diamond Jewellery", "Certified diamond pieces" },
				{ "Gold Jewellery", "Hallmarked gold jewellery" },
				{ "Silver Jewellery", "Sterling silver designs" } };
		for (String[] c : categories) {
			categoryRepository.save(Category.builder().name(c[0]).description(c[1]).build());
		}
	}

	private void seedProducts() {
		categoriesByName.clear();
		categoryRepository.findAll().forEach(c -> categoriesByName.put(c.getName(), c));

		product("Solitaire Diamond Ring", "Rings", MetalType.WHITE_GOLD, "18K", "3.200", "6500", "45000", "0", true, 6,
				"A brilliant-cut solitaire diamond in a classic four-prong white gold setting.");
		product("Floral Gold Ring", "Rings", MetalType.GOLD, "22K", "4.500", "3200", "0", "500", false, 12,
				"Hand-finished floral motif ring in hallmarked 22K gold.");
		product("Rose Gold Infinity Band", "Rings", MetalType.ROSE_GOLD, "18K", "2.800", "2400", "3500", "0", true, 9,
				"An infinity band with micro-set diamonds in warm rose gold.");
		product("Temple Gold Necklace", "Necklaces", MetalType.GOLD, "22K", "28.000", "16800", "0", "2000", true, 4,
				"Traditional temple necklace with Lakshmi motifs, perfect for festive wear.");
		product("Diamond Line Necklace", "Diamond Jewellery", MetalType.WHITE_GOLD, "18K", "12.500", "9500", "85000",
				"5000", true, 3, "A graduated line of round diamonds set in 18K white gold.");
		product("Pearl Drop Earrings", "Earrings", MetalType.GOLD, "18K", "3.600", "2800", "2200", "0", false, 15,
				"Freshwater pearl drops suspended from polished gold hooks.");
		product("Classic Gold Jhumkas", "Earrings", MetalType.GOLD, "22K", "8.200", "5200", "0", "0", true, 8,
				"Bell-shaped jhumkas with fine filigree work.");
		product("Diamond Stud Earrings", "Diamond Jewellery", MetalType.WHITE_GOLD, "18K", "1.800", "3000", "28000",
				"0", false, 10, "Timeless round diamond studs, 0.30 ct total.");
		product("Tennis Bracelet", "Bracelets", MetalType.WHITE_GOLD, "18K", "9.800", "7500", "62000", "3000", true, 2,
				"A flexible line of diamonds for everyday sparkle.");
		product("Twisted Gold Bangle Pair", "Bangles", MetalType.GOLD, "22K", "22.000", "11000", "0", "1500", false, 6,
				"A pair of twisted rope bangles in 22K gold.");
		product("Om Gold Pendant", "Pendants", MetalType.GOLD, "22K", "2.400", "1200", "0", "0", false, 20,
				"A small Om pendant, ideal as a gift.");
		product("Heart Silver Pendant", "Silver Jewellery", MetalType.SILVER, "925", "6.500", "650", "300", "0", false,
				25, "Sterling silver heart pendant with a cubic zirconia accent.");
		product("Rope Gold Chain", "Chains", MetalType.GOLD, "22K", "12.000", "4800", "0", "0", false, 9,
				"A sturdy 20-inch rope chain in 22K gold.");
		product("Platinum Couple Bands", "Wedding Jewellery", MetalType.PLATINUM, "950", "11.000", "8500", "0", "0",
				true, 5, "Matching platinum wedding bands with a brushed finish.");
		product("Bridal Kundan Set", "Wedding Jewellery", MetalType.GOLD, "22K", "48.000", "28000", "15000", "5000",
				true, 2, "Necklace and earrings set with kundan and polki work.");
		product("Oxidised Silver Anklets", "Silver Jewellery", MetalType.SILVER, "925", "24.000", "900", "0", "0",
				false, 4, "A pair of oxidised silver anklets with ghungroo detailing.");
	}

	private void product(String name, String category, MetalType metal, String purity, String weight, String making,
			String stone, String discount, boolean featured, int stock, String description) {
		MetalRate rate = metalRateRepository.findByMetalTypeAndPurityIgnoreCase(metal, purity).orElseThrow();
		Product p = Product.builder()
				.name(name)
				.sku("JC-" + metal.name().charAt(0) + String.format("%04d", productRepository.count() + 1))
				.description(description)
				.category(categoriesByName.get(category))
				.metalType(metal)
				.purity(purity)
				.weight(new BigDecimal(weight))
				.makingCharges(new BigDecimal(making))
				.stoneCharges(new BigDecimal(stone))
				.discount(new BigDecimal(discount))
				.tax(new BigDecimal("3.00"))
				.featured(featured)
				.build();
		pricingService.applyPrice(p, rate.getPricePerGram());
		p.setInventory(Inventory.builder().product(p).quantity(stock).lowStockThreshold(3).build());
		productRepository.save(p);
	}

	private void seedOffers() {
		LocalDate today = LocalDate.now();
		offerRepository.save(Offer.builder().code("WELCOME10").title("10% off your first order")
				.description("Up to Rs. 2,000 off on orders above Rs. 10,000").discountPercent(new BigDecimal("10"))
				.maxDiscountAmount(new BigDecimal("2000")).minOrderAmount(new BigDecimal("10000"))
				.startDate(today.minusDays(1)).endDate(today.plusYears(1)).build());
		offerRepository.save(Offer.builder().code("FESTIVE5").title("Festive 5% off")
				.description("5% off on all jewellery, no minimum").discountPercent(new BigDecimal("5"))
				.startDate(today.minusDays(1)).endDate(today.plusMonths(3)).build());
	}
}
