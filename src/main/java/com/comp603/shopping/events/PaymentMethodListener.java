package com.comp603.shopping.events;

import com.comp603.shopping.dao.PaymentMethodDAO;

/**
 * Interface for listening to payment method changes.
 */
public interface PaymentMethodListener {
    /**
     * Called when payment methods are added or removed.
     */
    void onPaymentMethodsChanged();
}