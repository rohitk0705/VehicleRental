let useLocalStorage = false;
let isReady = false;
let currentFleet = [];

document.addEventListener('DOMContentLoaded', () => {
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
            showDemoBanner();
            loadFleet();
        });
});

document.getElementById('addForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (!isReady) return;
    
    const type = document.getElementById('type').value;
    const id = document.getElementById('id').value;
    const brand = document.getElementById('brand').value;
    const extra = document.getElementById('extra').value;

    if (useLocalStorage) {
        const fleet = getLocalFleet();
        if (fleet.some(v => v.id === id)) {
            alert("ID already exists");
            return;
        }
        fleet.push({ id, type, brand, extra, rented: false });
        saveLocalFleet(fleet);
        document.getElementById('addForm').reset();
        document.getElementById('addModal').classList.remove('active');
        loadFleet();
    } else {
        fetch('/api/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `type=${type}&id=${id}&brand=${brand}&extra=${extra}`
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
            showDemoBanner();
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
}

function renderTable(data) {
    const tbody = document.querySelector('#fleetTable tbody');
    tbody.innerHTML = '';
    
    if (data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem; color: #64748b;">No vehicles found</td></tr>';
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

function showDemoBanner() {
    const banner = document.createElement('div');
    banner.style.background = '#f59e0b';
    banner.style.color = 'white';
    banner.style.textAlign = 'center';
    banner.style.padding = '0.75rem';
    banner.style.fontWeight = '600';
    banner.style.fontSize = '0.9rem';
    banner.style.position = 'fixed';
    banner.style.top = '0';
    banner.style.left = '0';
    banner.style.width = '100%';
    banner.style.zIndex = '2000';
    banner.innerHTML = '<i class="fas fa-wifi-slash"></i> Demo Mode: Running offline. Data is saved locally.';
    document.body.appendChild(banner);
    
    // Adjust sidebar and main content to not be hidden by banner
    document.querySelector('.sidebar').style.top = '40px';
    document.querySelector('.main-content').style.marginTop = '40px';
}

