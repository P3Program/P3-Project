const projectNotes = {};
let currentProjectId = null;


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
    document.getElementById('detail-email').textContent = email;
    document.getElementById('detail-est-time').textContent = (hours || '0') + ' hours';
    document.getElementById('detail-due-date').textContent = dueDate;
    document.getElementById('detail-desc').textContent = description || 'No description';

    // Load this project's notes from the database
    loadNotesFromDatabase(id);

    // Show the modal
    document.getElementById('project-detail-modal').style.display = 'flex';
}

// Get notes from the database and display them
function loadNotesFromDatabase(projectId) {
    const notesContainer = document.getElementById('detail-notes');
    notesContainer.innerHTML = '<em>Loading notes...</em>';

    // Ask the server for this project's notes
    fetch(`/projects/${projectId}/notes`)
        .then(response => response.json())
        .then(notes => {
            // If no notes exist, show message
            if (notes.length === 0) {
                notesContainer.innerHTML = '<em>No notes yet</em>';
                return;
            }

            // Create HTML for each note
            notesContainer.innerHTML = notes.map(note => {
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
                        <p>${note.text}</p>
                    </div>
                `;
            }).join('');
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
            'Content-Type': 'application/x-www-form-urlencoded'
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

// Close modals when clicking outside them
window.onclick = function(event) {
    const detailModal = document.getElementById('project-detail-modal');
    //const noteModal = document.getElementById('project-note-modal');

    if (event.target === detailModal) {
        detailModal.style.display = 'none';
    }
}
