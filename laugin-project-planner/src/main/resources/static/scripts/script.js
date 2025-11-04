document.addEventListener('DOMContentLoaded', () => {

    const containers = {
        review:   { el: document.getElementById('review-container'),  status: 'underReview' },
        progress: { el: document.getElementById('progress-container'),status: 'inProgress' },
        billing:  { el: document.getElementById('billing-container'), status: 'billing' }
    };

    document.querySelectorAll('#review-container .project-box, #progress-container .project-box, #billing-container .project-box').forEach(makeDraggable);

    function makeDraggable(card) {
        card.setAttribute('draggable', 'true');
        card.addEventListener('dragstart', onDragStart);
        card.addEventListener('dragend', onDragEnd);
    }

    let dragged = null;

    function onDragStart(e) {
        dragged = e.currentTarget;
        dragged.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/plain', dragged.dataset.id || '');
    }

    function onDragEnd() {
        if (dragged) dragged.classList.remove('dragging');
        dragged = null;
    }

    Object.values(containers).forEach(({ el }) => {
        el.addEventListener('dragover', (e) => {
            e.preventDefault();
            const afterEl = getAfterElement(el, e.clientY);
            if (!dragged) return;
            if (afterEl == null) el.appendChild(dragged);
            else el.insertBefore(dragged, afterEl);
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

            if (toStatus !== fromStatus) {
                try {
                    await fetch(`/api/projects/${id}/move?toStatus=${encodeURIComponent(toStatus)}`, {
                        method: 'PATCH'
                    });
                } catch (err) {
                    console.error('Kunne ikke flytte projekt', err);
                }
            }

            const orderedIds = [...el.querySelectorAll('.project-box')].map(c => Number(c.dataset.id));
            try {
                await fetch(`/api/projects/reorder?status=${encodeURIComponent(toStatus)}`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(orderedIds)
                });
            } catch (err) {
                console.error('Kunne ikke gemme rækkefølge', err);
            }
        });
    });

    function getAfterElement(container, y) {
        const els = [...container.querySelectorAll('.project-box:not(.dragging)')];
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
});
