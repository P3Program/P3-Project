// ========================================
// COMPLETE TOAST NOTIFICATION SYSTEM
// ========================================

// Create toast container on page load
document.addEventListener('DOMContentLoaded', () => {
    const toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 999999;
        pointer-events: none;
    `;
    document.body.appendChild(toastContainer);
});

// Toast function
function createToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) {
        console.error('Toast container not found!');
        return;
    }

    // Create toast element
    const toast = document.createElement('div');
    toast.style.cssText = `
        padding: 20px 30px;
        margin-bottom: 10px;
        border-radius: 8px;
        font-size: 16px;
        font-weight: 600;
        color: white;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
        transform: translateX(400px);
        opacity: 0;
        transition: all 0.3s ease;
        pointer-events: auto;
        min-width: 250px;
        text-align: center;
    `;

    // Set background based on type
    if (type === 'success') {
        toast.style.backgroundColor = '#4CAF50';
    } else if (type === 'error') {
        toast.style.backgroundColor = '#f44336';
    } else if (type === 'info') {
        toast.style.backgroundColor = '#2196F3';
    }

    toast.textContent = message;
    container.appendChild(toast);

    // Trigger slide-in animation
    setTimeout(() => {
        toast.style.transform = 'translateX(0)';
        toast.style.opacity = '1';
    }, 10);

    // Remove toast after 3 seconds
    setTimeout(() => {
        toast.style.transform = 'translateX(400px)';
        toast.style.opacity = '0';

        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 3000);
}

// Update changeProjectStatus to use new toast
async function changeProjectStatus(projectId, newStatus, statusLabel) {
    try {
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value ||
            document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('input[name="_csrf_header"]')?.value ||
            document.querySelector('meta[name="_csrf_header"]')?.content ||
            'X-CSRF-TOKEN';

        const response = await fetch(`/api/projects/${projectId}/move?toStatus=${encodeURIComponent(newStatus)}`, {
            method: 'PATCH',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken,
            },
        });

        if (response.ok) {
            const projectBox = document.querySelector(`.project-box[data-id="${projectId}"]`);
            if (projectBox) {
                const wrapper = projectBox.parentElement;
                projectBox.dataset.status = newStatus;

                const targetContainer = window.getContainerByStatus(newStatus);
                if (targetContainer && wrapper) {
                    targetContainer.appendChild(wrapper);
                }
            }

            // Show success toast
            createToast(`Moved to ${statusLabel}`, 'success');

            // Update status buttons immediately
            renderStatusButtons(newStatus, projectId);

        } else {
            throw new Error('Failed to update status');
        }
    } catch (error) {
        console.error('Error changing status:', error);
        createToast('Failed to change status', 'error');
    }
}

// Test function - call this in console to test: testToast()
function testToast() {
    createToast('This is a success message!', 'success');
    setTimeout(() => createToast('This is an error message!', 'error'), 500);
    setTimeout(() => createToast('This is an info message!', 'info'), 1000);
}

console.log('Toast system loaded. Test with: testToast()');