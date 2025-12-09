const API_BASE = '/api';

const api = {
    async getListings() {
        const res = await fetch(`${API_BASE}/listings/enabled`);
        return res.json();
    },

    async getBookings(renterId) {
        const res = await fetch(`${API_BASE}/bookings/renter/${renterId}`);
        return res.json();
    },

    async createBooking(data) {
        const res = await fetch(`${API_BASE}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return res.json();
    },

    async cancelBooking(id) {
        const res = await fetch(`${API_BASE}/bookings/${id}/cancel`, { method: 'PATCH' });
        return res.json();
    }
};
