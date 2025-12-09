const CURRENT_USER_ID = 1; // Simulated logged-in user

document.addEventListener('DOMContentLoaded', () => {
    loadListings();
    setupNavigation();
    setupModal();
});

async function loadListings() {
    const container = document.getElementById('listings-container');
    try {
        const listings = await api.getListings();
        container.innerHTML = listings.map(l => `
            <div class="card">
                <h3>${l.title}</h3>
                <p>${l.description || 'No description'}</p>
                <p class="price">€${l.dailyRate}/day</p>
                <button class="btn" onclick="openBookingModal(${l.id}, '${l.title}', ${l.dailyRate})">Book Now</button>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = '<p>Failed to load listings</p>';
    }
}

async function loadBookings() {
    const container = document.getElementById('bookings-container');
    try {
        const bookings = await api.getBookings(CURRENT_USER_ID);
        if (bookings.length === 0) {
            container.innerHTML = '<p>No bookings yet</p>';
            return;
        }
        container.innerHTML = bookings.map(b => `
            <div class="booking-item">
                <strong>Booking #${b.id}</strong>
                <span class="status ${b.status}">${b.status}</span>
                <p>Listing: ${b.listingId}</p>
                ${b.status === 'PENDING' ? `<button class="btn" onclick="cancelBooking(${b.id})">Cancel</button>` : ''}
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = '<p>Failed to load bookings</p>';
    }
}

function setupNavigation() {
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = e.target.getAttribute('href').substring(1);
            document.getElementById('listings').classList.toggle('hidden', target !== 'listings');
            document.getElementById('bookings').classList.toggle('hidden', target !== 'bookings');
            if (target === 'bookings') loadBookings();
        });
    });
}

function setupModal() {
    const modal = document.getElementById('modal');
    document.querySelector('.close').onclick = () => modal.classList.add('hidden');
    window.onclick = (e) => { if (e.target === modal) modal.classList.add('hidden'); };
}

function openBookingModal(listingId, title, dailyRate) {
    document.getElementById('modal-body').innerHTML = `
        <h3>Book: ${title}</h3>
        <form id="booking-form">
            <div class="form-group">
                <label>Start Date</label>
                <input type="date" id="startDate" required>
            </div>
            <div class="form-group">
                <label>End Date</label>
                <input type="date" id="endDate" required>
            </div>
            <button type="submit" class="btn">Confirm Booking</button>
        </form>
    `;
    document.getElementById('modal').classList.remove('hidden');
    
    document.getElementById('booking-form').onsubmit = async (e) => {
        e.preventDefault();
        await api.createBooking({
            listingId,
            requesterId: CURRENT_USER_ID,
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value
        });
        document.getElementById('modal').classList.add('hidden');
        alert('Booking created!');
    };
}

async function cancelBooking(id) {
    if (confirm('Cancel this booking?')) {
        await api.cancelBooking(id);
        loadBookings();
    }
}
