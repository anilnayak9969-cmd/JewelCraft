package com.example.JewelCraft.Dto;

import java.math.BigDecimal;

/** One bar/slice of a chart: a label, a count and (optionally) an amount. */
public record LabelValue(String label, long count, BigDecimal amount) {
}
