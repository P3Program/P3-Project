document.addEventListener('DOMContentLoaded', () => {

    const addTaskBtn = document.getElementById('addtask');
    const formModal = document.getElementById('task-form-modal');
    const closeFormBtn = document.getElementById('close-form');

    addTaskBtn.addEventListener('click', () => {
        formModal.style.display = 'flex';
        showPage(1);
        updatePriorityImmediately();
    });

    closeFormBtn.addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    // Close modal when clicking outside
    formModal.addEventListener('click', (e) => {
        if (e.target === formModal) {
            formModal.style.display = 'none';
        }
    });

    // Listens for change for caldera and or warranty and then runs updatePriorityImmediately
    document.querySelectorAll('input[name="caldera"], input[name="warranty"]').forEach(radio => {
        radio.addEventListener('change', updatePriorityImmediately);
    });

    // updatePriority takes the picked changes from the queryselectall and then runs calculatepriority and sets the priority
    function updatePriorityImmediately() {
        const calderaChecked = document.querySelector('input[name="caldera"]:checked');
        const warrantyChecked = document.querySelector('input[name="warranty"]:checked');

        if (calderaChecked && warrantyChecked) {
            const priority = calculatePriority();
            document.querySelector('select[name="priority"]').value = priority;
        }
    }

    // takes the updated values runs a simple if else to find which priority it should be set to
    function calculatePriority() {
        const caldera = document.querySelector('input[name="caldera"]:checked').value === "1";
        const warranty = document.querySelector('input[name="warranty"]:checked').value === "1";

        if (caldera && warranty) {
            return "Red";
        } else if (caldera && !warranty) {
            return "Yellow";
        } else {
            return "Green";
        }
    }

    // initiates our page to 1 for the forms 3 pages
    let currentPage = 1;

    // the next btn increments by one and updates our currentpage and runs showpage if we aren't already on page 3
    document.getElementById('next-btn').addEventListener('click', () => {
        if (currentPage < 3) {
            currentPage++;
            showPage(currentPage);
        }
    });

    // The prev btn decreases the page number by 1 if not already 1 and runs showPage
    document.getElementById('prev-btn').addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    // show page is what essentially when told what page we're on updates what page is shown
    function showPage(pageNum) {
        // Hide all pages
        document.querySelectorAll('.form-page').forEach(page => page.classList.add('hidden'));

        // Show current page
        document.getElementById(`page-${pageNum}`).classList.remove('hidden');

        // Update progress indicator
        document.querySelectorAll('.step').forEach((step, index) => {
            step.className = 'step'; // Reset all classes

            if (index + 1 < pageNum) {
                step.classList.add('completed'); // Mark previous steps as completed
            } else if (index + 1 === pageNum) {
                step.classList.add('active'); // Mark current step as active
            }
        });

        // Update buttons
        document.getElementById('prev-btn').style.display = pageNum === 1 ? 'none' : 'block';
        document.getElementById('next-btn').style.display = pageNum === 3 ? 'none' : 'block';
        document.getElementById('submit-btn').style.display = pageNum === 3 ? 'block' : 'none';
    }

    // Task Detail Modal Functionality
    const detailModal = document.getElementById('task-detail-modal');
    const deleteConfirmModal = document.getElementById('delete-confirm-modal');
    const closeDetailBtn = document.getElementById('close-detail');
    const editTaskBtn = document.getElementById('edit-task-btn');
    const saveTaskBtn = document.getElementById('save-task-btn');
    const deleteTaskBtn = document.getElementById('delete-task-btn');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');

    let currentProjectId = null;
    let isEditMode = false;

    // Close detail modal
    closeDetailBtn.addEventListener('click', () => {
        detailModal.style.display = 'none';
        isEditMode = false;
        resetEditMode();
    });

    // Close modal when clicking outside
    detailModal.addEventListener('click', (e) => {
        if (e.target === detailModal) {
            detailModal.style.display = 'none';
            isEditMode = false;
            resetEditMode();
        }
    });

    // Edit button - enable editing
    editTaskBtn.addEventListener('click', () => {
        isEditMode = true;
        enableEditMode();
    });

    // Delete button - show confirmation
    deleteTaskBtn.addEventListener('click', () => {
        deleteConfirmModal.style.display = 'flex';
    });

    // Cancel delete
    cancelDeleteBtn.addEventListener('click', () => {
        deleteConfirmModal.style.display = 'none';
    });

    // Close delete modal when clicking outside
    deleteConfirmModal.addEventListener('click', (e) => {
        if (e.target === deleteConfirmModal) {
            deleteConfirmModal.style.display = 'none';
        }
    });

    // Confirm delete
    confirmDeleteBtn.addEventListener('click', () => {
        if (currentProjectId) {
            window.location.href = `/projects/delete/${currentProjectId}`;
        }
    });

    function enableEditMode() {
        // Enable all input fields
        document.querySelectorAll('#detail-form input[readonly]').forEach(input => {
            input.removeAttribute('readonly');
        });
        document.querySelectorAll('#detail-form textarea[readonly]').forEach(textarea => {
            textarea.removeAttribute('readonly');
        });
        document.querySelectorAll('#detail-form select[disabled]').forEach(select => {
            select.removeAttribute('disabled');
        });

        // Hide display divs, show radio groups
        document.querySelectorAll('.detail-display').forEach(div => {
            div.style.display = 'none';
        });
        document.querySelectorAll('.detail-edit').forEach(group => {
            group.style.display = 'flex';
        });

        // Show save button, hide edit button
        editTaskBtn.style.display = 'none';
        saveTaskBtn.style.display = 'block';
    }

    function resetEditMode() {
        // Reset to view mode
        document.querySelectorAll('#detail-form input').forEach(input => {
            if (input.type !== 'hidden' && input.type !== 'radio') {
                input.setAttribute('readonly', 'readonly');
            }
        });
        document.querySelectorAll('#detail-form textarea').forEach(textarea => {
            textarea.setAttribute('readonly', 'readonly');
        });
        document.querySelectorAll('#detail-form select').forEach(select => {
            select.setAttribute('disabled', 'disabled');
        });

        // Show display divs, hide radio groups
        document.querySelectorAll('.detail-display').forEach(div => {
            div.style.display = 'block';
        });
        document.querySelectorAll('.detail-edit').forEach(group => {
            group.style.display = 'none';
        });

        editTaskBtn.style.display = 'block';
        saveTaskBtn.style.display = 'none';
    }
});

// Global function to open detail modal (called from onclick in HTML)
function openDetailModal(element) {
    const detailModal = document.getElementById('task-detail-modal');

    // Get data from element attributes
    const projectId = element.getAttribute('data-project-id');
    const title = element.getAttribute('data-project-title');
    const name = element.getAttribute('data-project-name');
    const phone = element.getAttribute('data-project-phone');
    const address = element.getAttribute('data-project-address');
    const email = element.getAttribute('data-project-email');
    const ssn = element.getAttribute('data-project-ssn');
    const caldera = element.getAttribute('data-project-caldera');
    const warranty = element.getAttribute('data-project-warranty');
    const priority = element.getAttribute('data-project-priority');
    const hours = element.getAttribute('data-project-hours');
    const dueDate = element.getAttribute('data-project-duedate');
    const description = element.getAttribute('data-project-description');
    const status = element.getAttribute('data-project-status');

    console.log('Opening modal for project:', projectId); // Debug

    // Populate modal fields
    document.getElementById('detail-id').value = projectId || '';
    document.getElementById('detail-title').value = title || '';
    document.getElementById('detail-name').value = name || '';
    document.getElementById('detail-phone').value = phone || '';
    document.getElementById('detail-address').value = address || '';
    document.getElementById('detail-email').value = email || '';
    document.getElementById('detail-ssn').value = ssn || '';
    document.getElementById('detail-priority').value = priority || 'Green';
    document.getElementById('detail-hours').value = hours || '';
    document.getElementById('detail-duedate').value = dueDate || '';
    document.getElementById('detail-description').value = description || '';
    document.getElementById('detail-status').value = status || '';

    // Set Caldera display and radio buttons
    const calderaDisplay = document.getElementById('caldera-display');
    calderaDisplay.textContent = caldera === '1' ? 'Yes' : 'No';
    const calderaRadio = document.querySelector(`input[name="caldera"][value="${caldera}"]`);
    if (calderaRadio) {
        calderaRadio.checked = true;
    }

    // Set Warranty display and radio buttons
    const warrantyDisplay = document.getElementById('warranty-display');
    warrantyDisplay.textContent = warranty === '1' ? 'Yes' : 'No';
    const warrantyRadio = document.querySelector(`input[name="warranty"][value="${warranty}"]`);
    if (warrantyRadio) {
        warrantyRadio.checked = true;
    }

    // Store current project ID globally
    window.currentProjectId = projectId;
    currentProjectId = projectId;

    // Reset to view mode
    document.querySelectorAll('#detail-form input').forEach(input => {
        if (input.type !== 'hidden' && input.type !== 'radio') {
            input.setAttribute('readonly', 'readonly');
        }
    });
    document.querySelectorAll('#detail-form textarea').forEach(textarea => {
        textarea.setAttribute('readonly', 'readonly');
    });
    document.querySelectorAll('#detail-form select').forEach(select => {
        select.setAttribute('disabled', 'disabled');
    });

    // Show display divs, hide radio groups
    document.querySelectorAll('.detail-display').forEach(div => {
        div.style.display = 'block';
    });
    document.querySelectorAll('.detail-edit').forEach(group => {
        group.style.display = 'none';
    });

    document.getElementById('edit-task-btn').style.display = 'block';
    document.getElementById('save-task-btn').style.display = 'none';

    // Show modal
    detailModal.style.display = 'flex';
}