document.addEventListener('DOMContentLoaded', () => {

    const addTaskBtn = document.getElementById('addtask');
    const formModal = document.getElementById('task-form-modal');
    const closeFormBtn = document.getElementById('close-form');

    addTaskBtn.addEventListener('click', () => {
        currentPage = 1;
        formModal.style.display = 'flex';
        showPage(1);
        updatePriorityImmediately()
    });


    closeFormBtn.addEventListener('click', () => {
        formModal.style.display = 'none';
    });

    //Listens for change for caldera and or warranty and then runs updatePriorityImmediately
    document.querySelectorAll('input[name="caldera"], input[name="warranty"]').forEach(radio => {
        radio.addEventListener('change', updatePriorityImmediately);
    });

    //updatePriority takes the picked changes form the queryselectall and then takes it runs calculatepriority and sets the priority
    function updatePriorityImmediately() {
        const calderaChecked = document.querySelector('input[name="caldera"]:checked');
        const warrantyChecked = document.querySelector('input[name="warranty"]:checked');

        if (calderaChecked && warrantyChecked) {
            const priority = calculatePriority();
            document.querySelector('select[name="priority"]').value = priority;
        }
    }

    //takes the updated values runs a simpel if else to find which priority it should be set to
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


    //initiates our page to 1 for the forms 4 pages
    let currentPage = 1;

    // the next btn incriments by one and updates our currentpage and runs showpage if we arent already on page 4
    document.getElementById('next-btn').addEventListener('click',() =>{
        if (currentPage < 3) {
            currentPage++;
            showPage(currentPage);
        }
    });

    // The prev btn decreases the page number by 1 if not already 1 and runs showPage
    document.getElementById('prev-btn').addEventListener('click',() =>{
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    // show page is what essentially when told what page were on updates what page is shown
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

});