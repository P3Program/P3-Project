const projectNotes = {};
let currentProjectId = null;
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');


function openProjectDetail(element) {
    // Get data from the project box that is clicked
    const id = element.getAttribute('data-id');
    const title = element.getAttribute('data-title');
    const name = element.getAttribute('data-name');
    const date = element.getAttribute('data-date');
    const caldera = element.getAttribute('data-caldera');
    const warranty = element.getAttribute('data-warranty');
    const ssn = element.getAttribute('data-ssn');
    const phone = element.getAttribute('data-phone');
    const address = element.getAttribute('data-address');
    const postCode = (element.dataset && element.dataset.postcode) || element.getAttribute('data-postcode') || element.getAttribute('data-post-code') || '';
    const email = element.getAttribute('data-email');
    const hours = element.getAttribute('data-hours');
    const dueDate = element.getAttribute('data-due-date');
    const description = element.getAttribute('data-description');

    console.log('Caldera raw value:', caldera);
    console.log('Warranty raw value:', warranty);

    // Store current project ID
    currentProjectId = id;

    // Set complete form action
    const completeForm = document.getElementById('complete-form');
    if (completeForm) {
        completeForm.action = `/projects/${id}/complete`;
        console.log('Set complete form action to:', completeForm.action);
    } else {
        console.error('Could not find complete-form element');
    }
    
        // Text content for the modal
    document.getElementById('detail-title').textContent = title;
    document.getElementById('detail-name').textContent = name;
    document.getElementById('detail-date').textContent = date;

    // Check for multiple possible values (1, '1', true, 'true')
    document.getElementById('detail-caldera').textContent =
        (caldera === '1' || caldera === 1 || caldera === 'true' || caldera === true) ? 'Yes' : 'No';
    document.getElementById('detail-warranty').textContent =
        (warranty === '1' || warranty === 1 || warranty === 'true' || warranty === true) ? 'Yes' : 'No';

    document.getElementById('detail-ssn').textContent = ssn;
    document.getElementById('detail-phone').textContent = phone;
    document.getElementById('detail-address').textContent = address;
    document.getElementById('detail-post-code').textContent = postCode || 'N/A';
    document.getElementById('detail-email').textContent = email;
    document.getElementById('detail-est-time').textContent = (hours || '0') + ' hours';
    document.getElementById('detail-due-date').textContent = dueDate;
    document.getElementById('detail-desc').textContent = description || 'No description';

    // Load this project's notes from the database
    loadNotesFromDatabase(id);

    // Show the modal
    document.getElementById('project-detail-modal').style.display = 'flex';
}

// Open the create/edit form modal prefilled for editing
function editProject() {
    if (!currentProjectId) {
        alert('No project selected to edit');
        return;
    }

    // Set form title and action
    const formTitle = document.getElementById('task-form-title');
    if (formTitle) formTitle.textContent = 'Edit Project';

    const taskForm = document.querySelector('.task-form');
    if (taskForm) {
        taskForm.action = `/projects/edit/${currentProjectId}`;
    }

    const idInput = document.getElementById('project-id');
    if (idInput) idInput.value = currentProjectId;

    // copy data from detail modal into the form fields
    const copyIfExists = (fromId, toId) => {
        const from = document.getElementById(fromId);
        const to = document.getElementById(toId);
        if (from && to) to.value = from.textContent.trim();
    };

    copyIfExists('detail-name', 'name');
    copyIfExists('detail-phone', 'phoneNum');
    copyIfExists('detail-address', 'address');
    copyIfExists('detail-post-code', 'postCode');
    copyIfExists('detail-email', 'email');
    copyIfExists('detail-ssn', 'ssn');
    copyIfExists('detail-title', 'title');

    const hoursEl = document.getElementById('detail-est-time');
    const hoursInput = document.getElementById('hours');
    if (hoursEl && hoursInput) {
        const txt = hoursEl.textContent.trim();
        hoursInput.value = txt.replace(/\s*hours?$/,'') || '';
    }
    copyIfExists('detail-due-date', 'estDueDate');
    copyIfExists('detail-desc', 'task-desc');

    const calderaVal = (document.getElementById('detail-caldera')?.textContent.trim() === 'Yes') ? '1' : '0';
    const calderaRadio = document.querySelector(`input[name='caldera'][value='${calderaVal}']`);
    if (calderaRadio) calderaRadio.checked = true;

    const warrantyVal = (document.getElementById('detail-warranty')?.textContent.trim() === 'Yes') ? '1' : '0';
    const warrantyRadio = document.querySelector(`input[name='warranty'][value='${warrantyVal}']`);
    if (warrantyRadio) warrantyRadio.checked = true;

    const box = document.querySelector(`.project-box[data-id='${currentProjectId}']`);
    if (box) {
        const pr = box.getAttribute('data-priority') || box.getAttribute('data-priority');
        const prioritySelect = document.getElementById('priority');
        if (prioritySelect && pr) prioritySelect.value = pr;
    }

    // hide detail modal and show the task form modal
    const detailModal = document.getElementById('project-detail-modal');
    const formModal = document.getElementById('task-form-modal');
    if (detailModal) detailModal.style.display = 'none';
    if (formModal) formModal.style.display = 'flex';

    // reset multi-step form
    document.getElementById('page-1')?.classList.remove('hidden');
    document.getElementById('page-2')?.classList.add('hidden');
    document.getElementById('page-3')?.classList.add('hidden');
    document.querySelectorAll('.form-progress .step').forEach(s => s.classList.remove('active'));
    document.querySelector('.form-progress .step[data-step="1"]')?.classList.add('active');

    // Change submit button text to match edit mode
    const submitBtn = document.getElementById('submit-btn');
    if (submitBtn) submitBtn.textContent = 'Edit Task';
}
// Escapes the rendered note to avoid code injection
function escapeHtml(str) {
    return str
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

// Get notes from the database and display them
function loadNotesFromDatabase(projectId) {
    const notesContainer = document.getElementById('detail-notes');
    notesContainer.innerHTML = '<em>Loading notes...</em>';

    // Ask the server for this project's notes
    fetch(`/projects/${projectId}/notes`)
        .then(response => response.json())
        .then(notes => {
            notesContainer.innerHTML = "";
            // If no notes exist, show message
            if (notes.length === 0) {
                notesContainer.innerHTML = '<em>No notes yet</em>';
                return;
            }

            notes.forEach(note => {
                const div = document.createElement("div");
                div.classList.add("note-item");

                const timestamp = new Date(note.timestamp).toLocaleString('is-IS', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });

                // Header remains HTML (safe because server-generated)
                const header = document.createElement("p");
                header.innerHTML = `<strong>${timestamp} - ${note.user}</strong>`;

                // NOTE TEXT SAFE
                const text = document.createElement("p");
                text.textContent = note.text; // ← Browser escapes everything

                div.appendChild(header);
                div.appendChild(text);

                notesContainer.appendChild(div);
            });
            // Create HTML for each note
            /* notesContainer.innerHTML = notes.map(note => {
                const timestamp = new Date(note.timestamp).toLocaleString('is-IS', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });

                return `
                    <div class="note-item">
                        <p><strong>${timestamp} - ${note.user}</strong></p>
                        <p>${escapeHtml(note.text)}</p>
                    </div>
                `;
            }).join(''); */

        })
        .catch(error => {
            console.error('Error loading notes:', error);
            notesContainer.innerHTML = '<em>Error loading notes</em>';
        });
}

// Open the note-modal to add a new note
function openAddNoteModal() {
    document.getElementById('project-note-modal').style.display = 'flex';
    document.getElementById('note-textarea').value = '';
}

// Close the add note modal
function closeAddNoteModal() {
    document.getElementById('project-note-modal').style.display = 'none';
}

// Save a new note to the database
function submitNote() {
    const noteText = document.getElementById('note-textarea').value.trim();

    if (!noteText) {
        alert('Please enter a note before submitting');
        return;
    }

    console.log('Current Project ID:', currentProjectId);
    console.log('Note Text:', noteText);

    fetch(`/projects/${currentProjectId}/addNote`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
        },
        body: `noteText=${encodeURIComponent(noteText)}`
    })
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response ok:', response.ok);
            return response.text();
        })
        .then(text => {
            console.log('Response text:', text);

            if (text && text !== 'null') {
                try {
                    const data = JSON.parse(text);
                    console.log('Parsed data:', data);
                } catch (e) {
                    console.log('Could not parse JSON:', e);
                }
            }

            loadNotesFromDatabase(currentProjectId);
            closeAddNoteModal();
        })
        .catch(error => {
            console.error('Error saving note:', error);
            alert('Error saving note. Please try again.');
        });
}

// Delete project
function deleteProject() {
    if (!currentProjectId) {
        alert('No project selected to delete');
        return;
    }

    if (confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
        window.location.href = `/projects/delete/${currentProjectId}`;
    }
}

// Close modals when clicking outside them
window.onclick = function(event) {
    const detailModal = document.getElementById('project-detail-modal');
    //const noteModal = document.getElementById('project-note-modal');

    if (event.target === detailModal) {
        detailModal.style.display = 'none';
    }
}
