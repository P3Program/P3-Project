document.addEventListener('DOMContentLoaded', () => {
    let activePriority = null;
    let activeZips = [];

    const filterButtons = document.querySelectorAll('.filter-button');
    const zipCheckboxes = document.querySelectorAll('.zip-checkbox');
    const zipDropdown = document.getElementById('zipDropdown');
    const zipSelected = document.querySelector('.zip-dropdown-selected');
    const selectAllBtn = document.querySelector('.zip-select-all');
    const clearAllBtn = document.querySelector('.zip-clear-all');


    if (zipDropdown) {
        zipDropdown.addEventListener('click', (e) => {
            zipDropdown.classList.toggle('open');
            e.stopPropagation();
        });

        document.addEventListener('click', () => {
            zipDropdown.classList.remove('open');
        });
    }

    // PRIORITY FILTER
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            const priority = button.dataset.priority;

            if (activePriority === priority) {
                activePriority = null;
                button.classList.remove('active');
            } else {
                activePriority = priority;
                filterButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');
            }

            applyFilters();
        });
    });

    // ZIP CODE FILTER
    zipCheckboxes.forEach(cb => {
        cb.addEventListener('change', (e) => {
            e.stopPropagation();
            recomputeActiveZipsAndLabel();
            applyFilters();
        });
    });

    if (selectAllBtn) {
        selectAllBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            zipCheckboxes.forEach(cb => cb.checked = true);
            recomputeActiveZipsAndLabel();
            applyFilters();
        });
    }

    if (clearAllBtn) {
        clearAllBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            zipCheckboxes.forEach(cb => cb.checked = false);
            recomputeActiveZipsAndLabel();
            applyFilters();
        });
    }

    function recomputeActiveZipsAndLabel() {
        activeZips = Array.from(zipCheckboxes)
            .filter(c => c.checked)
            .map(c => c.value);

        if (!zipSelected) return;

        if (activeZips.length === 0) {
            zipSelected.textContent = "Select postcode(s)";
        } else if (activeZips.length <= 3) {
            zipSelected.textContent = activeZips.join(", ");
        } else {
            zipSelected.textContent = `${activeZips.length} selected`;
        }
    }

    function applyFilters() {
        const projectBoxes = document.querySelectorAll('.project-box');

        projectBoxes.forEach(box => {
            let matchesPriority = true;
            if (activePriority) {
                matchesPriority = box.classList.contains(`priority-${activePriority}`);
            }

            let matchesZip = true;
            if (activeZips.length > 0) {
                const cardZip = box.dataset.postcode || "";
                matchesZip = activeZips.includes(cardZip);
            }

            if (matchesPriority && matchesZip) {
                box.classList.remove('hidden');
            } else {
                box.classList.add('hidden');
            }
        });
    }
});
