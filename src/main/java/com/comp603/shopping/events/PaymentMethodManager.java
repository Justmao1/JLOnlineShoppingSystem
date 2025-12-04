package com.comp603.shopping.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class to handle payment method change notifications between panels.
 */
public class PaymentMethodManager {
    private static PaymentMethodManager instance;
    private List<PaymentMethodListener> listeners;

    private PaymentMethodManager() {
        listeners = new ArrayList<>();
    }

    public static synchronized PaymentMethodManager getInstance() {
        if (instance == null) {
            instance = new PaymentMethodManager();
        }
        return instance;
    }

    public void addListener(PaymentMethodListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PaymentMethodListener listener) {
        listeners.remove(listener);
    }

    public void notifyPaymentMethodsChanged() {
        for (PaymentMethodListener listener : listeners) {
            listener.onPaymentMethodsChanged();
        }
    }
}