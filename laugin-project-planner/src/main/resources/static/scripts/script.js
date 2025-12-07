// Global variables
let currentProjectId = null;

document.addEventListener('DOMContentLoaded', () => {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const csrfHeader = document.querySelector('input[name="_csrf_header"]')?.value || 'X-CSRF-TOKEN';


    const containers = {
        review:   { el: document.getElementById('review-container'),  status: 'underReview' },
        progress: { el: document.getElementById('progress-container'),status: 'inProgress' },
        billing:  { el: document.getElementById('billing-container'), status: 'billing' }
    };

    function initializeDraggableCards() {
        document.querySelectorAll('.project-box').forEach(card => {
            // Only make draggable if not in allProjects
            if (card.dataset.status !== 'allProjects') {
                makeDraggable(card);
            }

            // Add click listener for modal
            card.addEventListener('click', (e) => {
                // Prevent modal from opening when dragging
                if (card.classList.contains('dragging')) {
                    return;
                }
                const projectId = card.dataset.id;
                currentProjectId = projectId;
                openProjectDetailModal(projectId);
            });
        });
    }

    // Call after delay for fragments to render
    setTimeout(initializeDraggableCards, 50);


    function makeDraggable(card) {
        card.setAttribute('draggable', 'true');

        // Remove old listeners
        card.removeEventListener('dragstart', onDragStart);
        card.removeEventListener('dragend', onDragEnd);

        // Add new listeners
        card.addEventListener('dragstart', onDragStart);
        card.addEventListener('dragend', onDragEnd);
    }

    let dragged = null;
    let draggedWrapper = null;

    function onDragStart(e) {
        dragged = e.currentTarget;
        draggedWrapper = dragged.parentElement;
        draggedWrapper.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/plain', dragged.dataset.id || '');
    }

    function onDragEnd() {
        if (draggedWrapper) draggedWrapper.classList.remove('dragging');
        dragged = null;
        draggedWrapper = null;
    }

    Object.values(containers).forEach(({ el }) => {
        el.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';

            if (!draggedWrapper) return;

            const afterEl = getAfterElement(el, e.clientY);

            try {
                // Only insert if it's actually a different position
                if (afterEl == null) {
                    if (draggedWrapper !== el.lastElementChild) {
                        el.appendChild(draggedWrapper);
                    }
                } else {
                    if (draggedWrapper !== afterEl && draggedWrapper.nextElementSibling !== afterEl) {
                        el.insertBefore(draggedWrapper, afterEl);
                    }
                }
            } catch (error) {
                console.error('Error moving element:', error);
            }
        });
        el.addEventListener('dragenter', () => el.classList.add('drag-over'));
        el.addEventListener('dragleave', () => el.classList.remove('drag-over'));

        el.addEventListener('drop', async (e) => {
            e.preventDefault();
            el.classList.remove('drag-over');
            if (!dragged) return;

            const toStatus = getStatusByContainer(el);
            const fromStatus = dragged.dataset.status;
            const id = dragged.dataset.id;

            dragged.dataset.status = toStatus;

            //makeDraggable(dragged);

            if (toStatus !== fromStatus) {
                try {
                    await fetch(`/api/projects/${id}/move?toStatus=${encodeURIComponent(toStatus)}`, {
                        method: 'PATCH',
                        credentials: 'include', // keep cookies
                        headers: {
                            'Content-Type': 'application/json',
                            [csrfHeader]: csrfToken,
                        },
                    });
                } catch (err) {
                    console.error('Kunne ikke flytte projekt', err);
                }
            }

            const orderedIds = [...el.querySelectorAll('.project-box')].map(c => Number(c.dataset.id));
            try {
                await fetch(`/api/projects/reorder?status=${encodeURIComponent(toStatus)}`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken,
                    },
                    body: JSON.stringify(orderedIds)
                });
            } catch (err) {
                console.error('Kunne ikke gemme rækkefølge', err);
            }
        });
    });

    function getAfterElement(container, y) {
        const els = [...container.children].filter(child =>
            !child.classList.contains('dragging')
        );

        let closest = { offset: Number.NEGATIVE_INFINITY, el: null };
        for (const el of els) {
            const box = el.getBoundingClientRect();
            const offset = y - box.top - box.height / 2;
            if (offset < 0 && offset > closest.offset) closest = { offset, el };
        }
        return closest.el;
    }

    function getStatusByContainer(el) {
        if (el === containers.review.el) return containers.review.status;
        if (el === containers.progress.el) return containers.progress.status;
        if (el === containers.billing.el) return containers.billing.status;
        return 'allProjects';
    }

    // Make getContainerByStatus available globally
    window.getContainerByStatus = function(status) {
        const containerMap = {
            'underReview': containers.review.el,
            'inProgress': containers.progress.el,
            'billing': containers.billing.el
        };
        return containerMap[status];
    };
});

// Modal functionality
function openProjectDetailModal(projectId) {
    const box = document.querySelector(`.project-box[data-id="${projectId}"]`);
    if (!box) return;

    const modal = document.getElementById('project-detail-modal');

    // Populate modal with project data
    document.getElementById('detail-title').textContent = box.dataset.title || 'No Title';
    document.getElementById('detail-name').textContent = box.dataset.name || 'N/A';
    document.getElementById('detail-phone').textContent = box.dataset.phone || 'N/A';
    document.getElementById('detail-email').textContent = box.dataset.email || 'N/A';
    document.getElementById('detail-address').textContent = box.dataset.address || 'N/A';
    document.getElementById('detail-ssn').textContent = box.dataset.ssn || 'N/A';
    document.getElementById('detail-desc').textContent = box.dataset.description || 'No description';
    document.getElementById('detail-date').textContent = box.dataset.date || 'N/A';
    document.getElementById('detail-caldera').textContent = box.dataset.caldera === '1' ? 'Yes' : 'No';
    document.getElementById('detail-warranty').textContent = box.dataset.warranty === '1' ? 'Yes' : 'No';
    document.getElementById('detail-est-time').textContent = box.dataset.hours ? `${box.dataset.hours} hours` : 'N/A';
    document.getElementById('detail-due-date').textContent = box.dataset.dueDate || 'N/A';

    const currentStatus = box.dataset.status;

    // Render status transition buttons
    renderStatusButtons(currentStatus, projectId);

    // Update complete form action
    const completeForm = document.getElementById('complete-form');
    completeForm.action = `/projects/${projectId}/complete`;

    // Load notes
    loadProjectNotes(projectId);

    modal.style.display = 'flex';
}

function renderStatusButtons(currentStatus, projectId) {
    // Remove existing status buttons if any
    const existingContainer = document.querySelector('.status-workflow-container');
    if (existingContainer) {
        existingContainer.remove();
    }

    // Only show status buttons if not in allProjects or completed
    if (currentStatus === 'allProjects') {
        return;
    }

    const modalFooter = document.querySelector('.modal-footer');

    // Create status workflow container
    const statusContainer = document.createElement('div');
    statusContainer.className = 'status-workflow-container';

    const statusButtons = document.createElement('div');
    statusButtons.className = 'status-buttons';

    // Define all possible statuses with their styling
    const statuses = [
        { key: 'underReview', label: 'Under Review', class: 'status-btn-review' },
        { key: 'inProgress', label: 'In Progress', class: 'status-btn-progress' },
        { key: 'billing', label: 'Billing', class: 'status-btn-billing' }
    ];

    // Create buttons for statuses that are NOT current
    statuses.forEach(status => {
        if (status.key !== currentStatus) {
            const btn = document.createElement('button');
            btn.className = `status-transition-btn ${status.class}`;
            btn.textContent = status.label;
            btn.onclick = () => changeProjectStatus(projectId, status.key, status.label);
            statusButtons.appendChild(btn);
        }
    });

    statusContainer.appendChild(statusButtons);

    // Insert after the button-group but before the complete form
    const completeForm = modalFooter.querySelector('.complete-form');
    modalFooter.insertBefore(statusContainer, completeForm);
}

// REMOVED OLD changeProjectStatus - will use the new one from toast.js

// REMOVED OLD showToast - will use the new one from toast.js

function loadProjectNotes(projectId) {
    fetch(`/projects/${projectId}/notes`)
        .then(res => res.json())
        .then(notes => {
            const notesContainer = document.getElementById('detail-notes');
            notesContainer.innerHTML = '';

            if (notes.length === 0) {
                notesContainer.innerHTML = '<p style="color: #888;">No notes yet.</p>';
                return;
            }

            notes.forEach(note => {
                const noteDiv = document.createElement('div');
                noteDiv.className = 'note-item';
                noteDiv.innerHTML = `
                    <p><strong>${note.username || 'Unknown'}</strong> - ${new Date(note.timestamp).toLocaleString()}</p>
                    <p>${note.noteText}</p>
                `;
                notesContainer.appendChild(noteDiv);
            });
        })
        .catch(err => console.error('Error loading notes:', err));
}

function openAddNoteModal() {
    document.getElementById('project-note-modal').style.display = 'flex';
}

function closeAddNoteModal() {
    document.getElementById('project-note-modal').style.display = 'none';
    document.getElementById('note-textarea').value = '';
}

function submitNote() {
    const noteText = document.getElementById('note-textarea').value.trim();
    if (!noteText) {
        alert('Please enter a note');
        return;
    }

    const csrfToken = document.querySelector('input[name="_csrf"]')?.value ||
        document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('input[name="_csrf_header"]')?.value ||
        document.querySelector('meta[name="_csrf_header"]')?.content ||
        'X-CSRF-TOKEN';

    fetch(`/projects/${currentProjectId}/addNote`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken
        },
        body: `noteText=${encodeURIComponent(noteText)}`
    })
        .then(res => res.json())
        .then(() => {
            closeAddNoteModal();
            loadProjectNotes(currentProjectId);
        })
        .catch(err => console.error('Error adding note:', err));
}

function editProject() {
    alert('Edit functionality not yet implemented');
}

function deleteProject() {
    if (!currentProjectId) return;

    if (confirm('Are you sure you want to delete this project?')) {
        window.location.href = `/projects/delete/${currentProjectId}`;
    }
}