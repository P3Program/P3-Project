// Animate numbers counting up
function animateValue(element, start, end, duration) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        const value = Math.floor(progress * (end - start) + start);
        element.textContent = value;
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}

// Calculate and display statistics
window.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        const redCount = parseInt(document.querySelector('.red .kpi-value').textContent) || 0;
        const yellowCount = parseInt(document.querySelector('.yellow .kpi-value').textContent) || 0;
        const greenCount = parseInt(document.querySelector('.green .kpi-value').textContent) || 0;

        const total = redCount + yellowCount + greenCount;

        // Animate total tasks
        const totalElement = document.getElementById('totalTasks');
        animateValue(totalElement, 0, total, 1500);

        // Add pulse animation to KPI values
        document.querySelectorAll('.kpi-value').forEach((el, index) => {
            setTimeout(() => {
                el.style.animation = 'countUp 0.6s ease forwards';
            }, 300 + (index * 200));
        });
    }, 500);

    // Click handlers for KPI cards
    const kpiCards = document.querySelectorAll('.kpi-card');
    const summaryView = document.getElementById('summary-view');
    const detailView = document.getElementById('detail-view');
    const backBtn = document.getElementById('back-btn');
    const detailTitle = document.getElementById('detail-title');

    const redContainer = document.getElementById('red-projects-container');
    const yellowContainer = document.getElementById('yellow-projects-container');
    const greenContainer = document.getElementById('green-projects-container');

    kpiCards.forEach(card => {
        card.addEventListener('click', () => {
            const priority = card.getAttribute('data-priority');

            // Hide summary, show detail view
            summaryView.style.display = 'none';
            detailView.style.display = 'block';

            // Hide all project containers
            redContainer.style.display = 'none';
            yellowContainer.style.display = 'none';
            greenContainer.style.display = 'none';

            // Show the selected priority container and update title
            if (priority === 'red') {
                redContainer.style.display = 'grid';
                detailTitle.textContent = 'Critical Priority Tasks';
            } else if (priority === 'yellow') {
                yellowContainer.style.display = 'grid';
                detailTitle.textContent = 'Important Priority Tasks';
            } else if (priority === 'green') {
                greenContainer.style.display = 'grid';
                detailTitle.textContent = 'Normal Priority Tasks';
            }
        });
    });

    // Back button handler
    backBtn.addEventListener('click', () => {
        // Hide detail view, show summary
        detailView.style.display = 'none';
        summaryView.style.display = 'block';

        // Hide all project containers
        redContainer.style.display = 'none';
        yellowContainer.style.display = 'none';
        greenContainer.style.display = 'none';
    });

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
            window.location.href = `/projects/delete-kpi/${currentProjectId}`;
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
function openDetailModalKpi(element) {
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

// Add subtle parallax effect on mouse move (only in summary view)
document.addEventListener('mousemove', (e) => {
    const summaryView = document.getElementById('summary-view');

    // Only apply parallax if summary view is visible
    if (summaryView && summaryView.style.display !== 'none') {
        const cards = document.querySelectorAll('.task-display > div');
        const x = e.clientX / window.innerWidth - 0.5;
        const y = e.clientY / window.innerHeight - 0.5;

        cards.forEach((card, index) => {
            const intensity = (index + 1) * 5;
            const baseTransform = 'translateY(-8px) scale(1.02)';
            const parallaxTransform = `perspective(1000px) rotateX(${y * intensity}deg) rotateY(${-x * intensity}deg)`;

            // Check if card is being hovered
            if (card.matches(':hover')) {
                card.style.transform = `${baseTransform} ${parallaxTransform}`;
            }
        });
    }
});

// Reset transform when mouse leaves
document.addEventListener('mouseleave', () => {
    const cards = document.querySelectorAll('.task-display > div');
    cards.forEach(card => {
        card.style.transform = '';
    });
});