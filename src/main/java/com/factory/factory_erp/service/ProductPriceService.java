package com.factory.factory_erp.service;

import com.factory.factory_erp.dto.ProductPriceDTO;
import com.factory.factory_erp.entity.ProductPrice;
import com.factory.factory_erp.entity.User;
import com.factory.factory_erp.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing product prices with historical tracking.
 * Maintains price history to ensure reports and past records retain accurate historical data.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPriceService {
    
    private final ProductPriceRepository productPriceRepository;
    
    /**
     * Get the active price for a product on a specific date
     */
    public Optional<ProductPrice> getActivePriceOn(Long productId, LocalDate date) {
        List<ProductPrice> prices = productPriceRepository.findByProductIdAndStatus(
            productId, 
            ProductPrice.PriceStatus.ACTIVE
        );
        
        return prices.stream()
            .filter(price -> price.isActiveOn(date))
            .findFirst();
    }
    
    /**
     * Get the current active price for a product (today's price)
     */
    public Optional<ProductPrice> getCurrentPrice(Long productId) {
        return getActivePriceOn(productId, LocalDate.now());
    }
    
    /**
     * Get all active prices for a product that are valid on a specific date
     */
    public List<ProductPrice> getPriceHistoryForPeriod(Long productId, LocalDate startDate, LocalDate endDate) {
        return productPriceRepository.findByProductIdAndPeriod(productId, startDate, endDate);
    }
    
    /**
     * Create a new price for a product.
     * If there's an active price, it will be marked as superseded and its end_date set to yesterday.
     */
    public ProductPrice createPrice(Long productId, BigDecimal amount, String currency, 
                                    User operator, LocalDate startDate, String notes) {
        // Expire any existing active price
        Optional<ProductPrice> currentPrice = getCurrentPrice(productId);
        if (currentPrice.isPresent()) {
            expirePrice(currentPrice.get(), startDate.minusDays(1));
        }
        
        // Create new price
        ProductPrice newPrice = ProductPrice.builder()
            .productId(productId)
            .amount(amount)
            .currency(currency)
            .operator(operator)
            .startDate(startDate)
            .status(ProductPrice.PriceStatus.ACTIVE)
            .notes(notes)
            .build();
        
        return productPriceRepository.save(newPrice);
    }
    
    /**
     * Expire a price (mark it as no longer active)
     */
    public ProductPrice expirePrice(ProductPrice price, LocalDate expiryDate) {
        price.expire(expiryDate);
        return productPriceRepository.save(price);
    }
    
    /**
     * Get all price changes for a product (sorted by date)
     */
    public List<ProductPrice> getPriceChangeHistory(Long productId) {
        return productPriceRepository.findByProductIdOrderByStartDateDesc(productId);
    }
    
    /**
     * Mark a price as superseded by a newer price
     */
    public ProductPrice markAsSuperseeded(ProductPrice price) {
        price.setStatus(ProductPrice.PriceStatus.SUPERSEDED);
        return productPriceRepository.save(price);
    }
    
    /**
     * Get all archived prices for a product
     */
    public List<ProductPrice> getArchivedPrices(Long productId) {
        return productPriceRepository.findByProductIdAndStatus(
            productId, 
            ProductPrice.PriceStatus.ARCHIVED
        );
    }
}
