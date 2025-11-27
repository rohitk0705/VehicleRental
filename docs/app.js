let useLocalStorage = false;
let isReady = false;
let currentFleet = [];
let fleetChart = null;
let revenueChart = null;
let currentCurrency = 'INR';
let exchangeRates = { 'INR': 1 };
let currencySymbols = { 'INR': '₹' };

// Auth Check
const currentUser = JSON.parse(localStorage.getItem('currentUser'));
if (!currentUser) {
    window.location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', () => {
    // Initialize Settings
    initSettings();

    // Set User Info
    if(currentUser) {
        document.getElementById('userName').textContent = currentUser.name;
        document.getElementById('userRole').textContent = currentUser.role;
        document.getElementById('userAvatar').src = currentUser.avatar;
    }

    // Logout Logic
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
    });

    // Navigation Logic
    const navLinks = document.querySelectorAll('.nav-link');
    const views = document.querySelectorAll('.view-section');
    
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-view');
            
            // Update Nav
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
            
            // Update View
            views.forEach(v => v.classList.add('hidden'));
            document.getElementById(`view-${targetId}`).classList.remove('hidden');
            
            // Update Title
            document.getElementById('pageTitle').textContent = link.textContent.trim();

            // Render Charts if Analytics
            if(targetId === 'analytics') renderCharts();
        });
    });

    // Settings Logic
    document.getElementById('resetDataBtn').addEventListener('click', () => {
        if(confirm('Are you sure? This will delete all local data and log you out.')) {
            localStorage.clear();
            window.location.href = 'login.html';
        }
    });

    // Dark Mode Toggle
    const darkModeToggle = document.getElementById('darkModeToggle');
    darkModeToggle.addEventListener('change', (e) => {
        if(e.target.checked) {
            document.body.classList.add('dark-mode');
            localStorage.setItem('darkMode', 'true');
        } else {
            document.body.classList.remove('dark-mode');
            localStorage.setItem('darkMode', 'false');
        }
    });

    // Currency Toggle
    const currencySelect = document.querySelector('.setting-select');
    currencySelect.addEventListener('change', (e) => {
        // Only INR supported now
        currentCurrency = 'INR';
        localStorage.setItem('currency', currentCurrency);
        renderTable(currentFleet);
        updateStats(currentFleet);
        renderCharts();
    });

    const submitBtn = document.querySelector('#addForm button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = "Connecting...";

    // Modal Logic
    const modal = document.getElementById('addModal');
    const openBtn = document.getElementById('openAddModal');
    const closeBtns = document.querySelectorAll('.close-modal');

    openBtn.addEventListener('click', () => modal.classList.add('active'));
    closeBtns.forEach(btn => btn.addEventListener('click', () => modal.classList.remove('active')));
    modal.addEventListener('click', (e) => {
        if(e.target === modal) modal.classList.remove('active');
    });

    // Search Logic
    document.getElementById('searchInput').addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = currentFleet.filter(v => 
            v.id.toLowerCase().includes(term) || 
            v.brand.toLowerCase().includes(term) || 
            v.type.toLowerCase().includes(term)
        );
        renderTable(filtered);
    });

    // Check if API is available
    fetch('/api/fleet')
        .then(response => {
            if (!response.ok || !response.headers.get("content-type")?.includes("application/json")) {
                throw new Error("API unavailable");
            }
            return response.json();
        })
        .then(data => {
            isReady = true;
            submitBtn.disabled = false;
            submitBtn.textContent = 'Add Vehicle';
            currentFleet = data;
            renderTable(data);
            updateStats(data);
        })
        .catch(err => {
            console.log("API not found, switching to Demo Mode (LocalStorage)");
            useLocalStorage = true;
            isReady = true;
            submitBtn.disabled = false;
            submitBtn.textContent = 'Add Vehicle';
            // showDemoBanner(); // Removed as per request
            loadFleet();
        });
});

function initSettings() {
    // Dark Mode
    const isDark = localStorage.getItem('darkMode') === 'true';
    if(isDark) {
        document.body.classList.add('dark-mode');
        document.getElementById('darkModeToggle').checked = true;
    }

    // Currency
    const currencySelect = document.querySelector('.setting-select');
    if(currencySelect) {
        currencySelect.value = currentCurrency;
    }
}

function formatPrice(price) {
    const val = (price || 0) * exchangeRates[currentCurrency];
    return currencySymbols[currentCurrency] + val.toLocaleString(undefined, {minimumFractionDigits: 0, maximumFractionDigits: 0});
}

function renderCharts() {
    const ctx1 = document.getElementById('fleetChart').getContext('2d');
    const ctx2 = document.getElementById('revenueChart').getContext('2d');

    // Count types
    const counts = { Car: 0, Bike: 0, Truck: 0 };
    currentFleet.forEach(v => {
        if(counts[v.type] !== undefined) counts[v.type]++;
    });

    if(fleetChart) fleetChart.destroy();
    fleetChart = new Chart(ctx1, {
        type: 'doughnut',
        data: {
            labels: ['Car', 'Bike', 'Truck'],
            datasets: [{
                data: [counts.Car, counts.Bike, counts.Truck],
                backgroundColor: ['#4361ee', '#10b981', '#f59e0b'],
                borderWidth: 0
            }]
        },
        options: { 
            responsive: true, 
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: { color: document.body.classList.contains('dark-mode') ? '#cbd5e1' : '#64748b' }
                }
            }
        }
    });

    // Mock Revenue Data scaled by currency
    const baseData = [1200, 1900, 3000, 5000, 2000, 3000, 4500];
    const scaledData = baseData.map(v => v * exchangeRates[currentCurrency]);

    if(revenueChart) revenueChart.destroy();
    revenueChart = new Chart(ctx2, {
        type: 'line',
        data: {
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            datasets: [{
                label: `Revenue (${currentCurrency})`,
                data: scaledData,
                borderColor: '#4361ee',
                tension: 0.4,
                pointBackgroundColor: '#4361ee'
            }]
        },
        options: { 
            responsive: true, 
            maintainAspectRatio: false,
            scales: {
                x: { ticks: { color: document.body.classList.contains('dark-mode') ? '#94a3b8' : '#64748b' } },
                y: { ticks: { color: document.body.classList.contains('dark-mode') ? '#94a3b8' : '#64748b' } }
            },
            plugins: {
                legend: {
                    labels: { color: document.body.classList.contains('dark-mode') ? '#cbd5e1' : '#64748b' }
                }
            }
        }
    });
}

document.getElementById('addForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (!isReady) return;
    
    const type = document.getElementById('type').value;
    const id = document.getElementById('id').value;
    const brand = document.getElementById('brand').value;
    const extra = document.getElementById('extra').value;
    const price = document.getElementById('price').value;

    if (useLocalStorage) {
        const fleet = getLocalFleet();
        if (fleet.some(v => v.id === id)) {
            alert("ID already exists");
            return;
        }
        fleet.push({ id, type, brand, extra, price, rented: false });
        saveLocalFleet(fleet);
        document.getElementById('addForm').reset();
        document.getElementById('addModal').classList.remove('active');
        loadFleet();
    } else {
        fetch('/api/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `type=${type}&id=${id}&brand=${brand}&extra=${extra}&price=${price}`
        })
        .then(response => {
            if (!response.ok) throw new Error("Server Error");
            return response.text();
        })
        .then(msg => {
            if(msg === 'Added') {
                document.getElementById('addForm').reset();
                document.getElementById('addModal').classList.remove('active');
                loadFleet();
            } else {
                alert(msg);
            }
        })
        .catch(err => {
            alert("Error connecting to server. Switching to Demo Mode.");
            useLocalStorage = true;
            // showDemoBanner();
        });
    }
});

function loadFleet() {
    if (useLocalStorage) {
        const data = getLocalFleet();
        currentFleet = data;
        renderTable(data);
        updateStats(data);
    } else {
        fetch('/api/fleet')
        .then(response => response.json())
        .then(data => {
            currentFleet = data;
            renderTable(data);
            updateStats(data);
        });
    }
}

function updateStats(data) {
    document.getElementById('totalCount').textContent = data.length;
    document.getElementById('availableCount').textContent = data.filter(v => !v.rented).length;
    document.getElementById('rentedCount').textContent = data.filter(v => v.rented).length;
    
    // Calculate Revenue (Daily revenue from rented vehicles)
    const revenue = data.reduce((acc, v) => acc + (v.rented ? parseFloat(v.price || 0) : 0), 0);
    document.getElementById('revenueCount').textContent = formatPrice(revenue);
}

function renderTable(data) {
    const tbody = document.querySelector('#fleetTable tbody');
    tbody.innerHTML = '';
    
    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 2rem; color: #64748b;">No vehicles found</td></tr>';
        return;
    }

    data.forEach(v => {
        const tr = document.createElement('tr');
        
        // Icon based on type
        let icon = 'fa-car';
        if(v.type === 'Bike') icon = 'fa-motorcycle';
        if(v.type === 'Truck') icon = 'fa-truck';

        tr.innerHTML = `
            <td><span style="font-family: monospace; font-weight: 600;">${v.id}</span></td>
            <td>
                <div class="vehicle-info">
                    <div class="vehicle-icon"><i class="fas ${icon}"></i></div>
                    <div class="vehicle-text">
                        <span class="vehicle-brand">${v.brand}</span>
                        <span class="vehicle-type">${v.type}</span>
                    </div>
                </div>
            </td>
            <td>${v.extra}</td>
            <td><span style="font-weight: 600;">${formatPrice(v.price)}</span></td>
            <td>
                <span class="status-badge ${v.rented ? 'rented' : 'available'}">
                    <span class="status-dot"></span>
                    ${v.rented ? 'Rented' : 'Available'}
                </span>
            </td>
            <td>
                ${v.rented 
                    ? `<button onclick="returnVehicle('${v.id}')" class="action-btn return" title="Return Vehicle"><i class="fas fa-undo"></i> Return</button>`
                    : `<button onclick="rentVehicle('${v.id}')" class="action-btn rent" title="Rent Vehicle"><i class="fas fa-key"></i> Rent</button>`
                }
                <button onclick="deleteVehicle('${v.id}')" class="action-btn delete" title="Delete Vehicle"><i class="fas fa-trash"></i></button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function rentVehicle(id) {
    if (useLocalStorage) {
        const fleet = getLocalFleet();
        const v = fleet.find(v => v.id === id);
        if (v) {
            v.rented = true;
            saveLocalFleet(fleet);
            loadFleet();
        }
    } else {
        fetch('/api/rent', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${id}`
        })
        .then(response => response.text())
        .then(msg => {
            loadFleet();
        });
    }
}

function returnVehicle(id) {
    if (useLocalStorage) {
        const fleet = getLocalFleet();
        const v = fleet.find(v => v.id === id);
        if (v) {
            v.rented = false;
            saveLocalFleet(fleet);
            loadFleet();
        }
    } else {
        fetch('/api/return', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${id}`
        })
        .then(response => response.text())
        .then(msg => {
            loadFleet();
        });
    }
}

function deleteVehicle(id) {
    if (!confirm("Are you sure you want to delete vehicle " + id + "?")) return;

    if (useLocalStorage) {
        let fleet = getLocalFleet();
        fleet = fleet.filter(v => v.id !== id);
        saveLocalFleet(fleet);
        loadFleet();
    } else {
        fetch('/api/delete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${id}`
        })
        .then(response => response.text())
        .then(msg => {
            loadFleet();
        });
    }
}

// Local Storage Helpers
function getLocalFleet() {
    return JSON.parse(localStorage.getItem('vehicle_fleet') || '[]');
}

function saveLocalFleet(fleet) {
    localStorage.setItem('vehicle_fleet', JSON.stringify(fleet));
}

// Removed showDemoBanner function as requested

