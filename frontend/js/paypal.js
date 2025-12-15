function initPayPalButton(amount, bookingId) {
    console.log('Initializing PayPal button with amount:', amount, 'bookingId:', bookingId);
    
    if (!window.paypal) {
        console.error('PayPal SDK not loaded');
        showToast('PayPal not available', 'error');
        return;
    }

    const container = document.getElementById('paypal-button-container');
    if (!container) {
        console.error('PayPal container not found');
        return;
    }

    // Clear any existing buttons
    container.innerHTML = '';

    paypal.Buttons({
        createOrder: function(data, actions) {
            console.log('Creating PayPal order...');
            return fetch('http://localhost:8081/api/payments/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: amount.toString() })
            })
            .then(response => {
                console.log('Payment create response:', response);
                return response.json();
            })
            .then(order => {
                console.log('Order created:', order);
                return order.id;
            })
            .catch(error => {
                console.error('Error creating order:', error);
                throw error;
            });
        },
        onApprove: function(data, actions) {
            console.log('Payment approved, capturing...');
            return fetch(`http://localhost:8081/api/payments/capture/${data.orderID}`, {
                method: 'POST'
            })
            .then(response => response.json())
            .then(details => {
                console.log('Payment captured:', details);
                onPaymentSuccess(bookingId);
            });
        },
        onError: function(err) {
            console.error('PayPal error:', err);
            showToast('Payment failed. Please try again.', 'error');
        }
    }).render('#paypal-button-container')
    .then(() => {
        console.log('PayPal button rendered successfully');
    })
    .catch(error => {
        console.error('Error rendering PayPal button:', error);
        showToast('Failed to load PayPal button', 'error');
    });
}
