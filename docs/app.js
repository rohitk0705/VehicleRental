document.addEventListener('DOMContentLoaded', loadFleet);

document.getElementById('addForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const type = document.getElementById('type').value;
    const id = document.getElementById('id').value;
    const brand = document.getElementById('brand').value;
    const extra = document.getElementById('extra').value;

    fetch('/api/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `type=${type}&id=${id}&brand=${brand}&extra=${extra}`
    })
    .then(response => response.text())
    .then(msg => {
        alert(msg);
        if(msg === 'Added') {
            document.getElementById('addForm').reset();
            loadFleet();
        }
    });
});

function loadFleet() {
    fetch('/api/fleet')
    .then(response => response.json())
    .then(data => {
        const tbody = document.querySelector('#fleetTable tbody');
        tbody.innerHTML = '';
        
        data.forEach(v => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${v.id}</td>
                <td>${v.type}</td>
                <td>${v.brand}</td>
                <td>${v.extra}</td>
                <td><span class="status ${v.rented ? 'rented' : 'available'}">${v.rented ? 'Rented' : 'Available'}</span></td>
                <td>
                    ${v.rented 
                        ? `<button onclick="returnVehicle('${v.id}')" class="btn-action return">Return</button>`
                        : `<button onclick="rentVehicle('${v.id}')" class="btn-action rent">Rent</button>`
                    }
                </td>
            `;
            tbody.appendChild(tr);
        });
    });
}

function rentVehicle(id) {
    fetch('/api/rent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}`
    })
    .then(response => response.text())
    .then(msg => {
        alert(msg);
        loadFleet();
    });
}

function returnVehicle(id) {
    fetch('/api/return', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `id=${id}`
    })
    .then(response => response.text())
    .then(msg => {
        alert(msg);
        loadFleet();
    });
}
