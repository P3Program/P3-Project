const msg = document.body.dataset.message;
const type = document.body.dataset.messageType || 'success';

if (msg) {
    showToast(msg, type);
}

document.addEventListener('DOMContentLoaded', () => {

    // Modal Elements
    const addTaskBtn = document.getElementById('addtask');
    const formModal = document.getElementById('task-form-modal');
    const closeFormBtn = document.getElementById('close-form');

    // Open Modal
    addTaskBtn.addEventListener('click', () => {
        formModal.style.display = 'flex';
        showPage(1);
        updatePriorityImmediately();
        currentPage = 1;
    });

    // Close Modal
    closeFormBtn.addEventListener('click', () => {
        formModal.style.display = 'none';
        document.querySelector(".task-form").reset(); // Clear all fields

    });

    // Prevent Enter key from submitting the form on text inputs
    const taskForm = document.querySelector('.task-form');
    taskForm.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA')) {
            e.preventDefault();
        }
    });

    // Listen for changes on Caldera and Warranty radio buttons
    document.querySelectorAll('input[name="caldera"], input[name="warranty"]').forEach(radio => {
        radio.addEventListener('change', updatePriorityImmediately);
    });

    // Update Priority based on Caldera and Warranty selections
    function updatePriorityImmediately() {
        const calderaChecked = document.querySelector('input[name="caldera"]:checked');
        const warrantyChecked = document.querySelector('input[name="warranty"]:checked');

        if (calderaChecked && warrantyChecked) {
            const priority = calculatePriority();
            document.querySelector('select[name="priority"]').value = priority;
        }
    }

    // Calculate priority based on business logic
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

    // Multi-step form navigation
    let currentPage = 1;

    // Next button - advance to next page
    document.getElementById('next-btn').addEventListener('click', () => {
        if (validatePage(currentPage)) {  // ← ADDED validation check
            if (currentPage < 3) {
                currentPage++;
                showPage(currentPage);
            }
        }
    });

    // Previous button - go back one page
    document.getElementById('prev-btn').addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    // Makes the progress indicators at the top clickable
    document.querySelectorAll('.step').forEach((step, index) => {
        step.addEventListener('click', () => {
            const targetPage = index + 1; // Steps are 0-indexed, pages are 1-indexed
            if (targetPage > currentPage) {
                if (validatePage(currentPage)) {
                    currentPage = targetPage;
                    showPage(currentPage);
                }
            } else {
                // Allow going back without validation
                currentPage = targetPage;
                showPage(currentPage);
            }
        });
    });

    // Validate going to the next page
    function validatePage(pageNum) {
        const page = document.getElementById('page-' + pageNum);
        const inputs = page.querySelectorAll('input, textarea, select');

        for (const input of inputs) {
            if (!input.checkValidity()) {
                input.reportValidity();
                return false; // Stop at first invalid field
            }
        }

        return true;
    }

    // Show the specified page and update UI
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

        // Update button visibility
        document.getElementById('prev-btn').style.display = pageNum === 1 ? 'none' : 'block';
        document.getElementById('next-btn').style.display = pageNum === 3 ? 'none' : 'block';
        document.getElementById('submit-btn').style.display = pageNum === 3 ? 'block' : 'none';
    }

});

function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.remove(), 3000);
}