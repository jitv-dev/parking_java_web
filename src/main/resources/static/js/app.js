document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("searchInput");

    if (searchInput) {
        searchInput.addEventListener("keyup", function() {
            const filter = this.value.toUpperCase();
            const table = document.getElementById("vehiclesTable");
            const tr = table.getElementsByTagName("tr");
            let countVisible = 0;

            for (let i = 1; i < tr.length; i++) {
                if (tr[i].id === "noResults") continue;
                const td = tr[i].getElementsByTagName("td")[0];
                if (td) {
                    const txtValue = td.textContent || td.innerText;
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                        countVisible++;
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }

            const noResultsRow = document.getElementById("noResults");
            if (noResultsRow) {
                noResultsRow.style.display = countVisible === 0 ? "" : "none";
            }
        });
    }

    // --- Lógica del Cronómetro del Modal ---
    const freezeTimer = document.getElementById('freezeTimer');

    if (freezeTimer) {
        let timeLeft = 300;

        const countdown = setInterval(() => {
            timeLeft--;

            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;

            freezeTimer.textContent =
                `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            // Si el tiempo llega a cero
            if (timeLeft <= 0) {
                clearInterval(countdown);
                freezeTimer.textContent = "EXPIRADO - Debe cancelar y recalcular";
                freezeTimer.style.color = "#d93025"; // Rojo para indicar expiración

                // Opcional: Deshabilitar el botón de pago si expiró
                const payButton = document.querySelector('button[type="submit"][style*="background: #ffffff"]');
                if(payButton) {
                    payButton.disabled = true;
                    payButton.style.opacity = "0.5";
                    payButton.textContent = "Cotización Expirada";
                }
            }
        }, 1000);
    }
});