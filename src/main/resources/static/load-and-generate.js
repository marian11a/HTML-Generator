document.addEventListener("DOMContentLoaded", function() {
    fetchFileList();
});

function fetchFileList() {
    fetch('/list-files', {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        const fileList = document.getElementById('fileList');
        data.forEach(file => {
            const listItem = document.createElement('li');
            listItem.textContent = file;
            listItem.addEventListener('click', function() {
                openHTMLFileInNewTab(file);
            });
            fileList.appendChild(listItem);
        });
    })
    .catch(error => console.error('Error fetching file list:', error));
}

function generateHTML() {
    const generateButton = document.getElementById('generateButton');
    generateButton.disabled = true;
    generateButton.textContent = 'Generating...';

    const htmlContent = document.getElementById('textInput').value;
    fetch('/generate-html', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(htmlContent)
    })
    .then(response => {
        if (response.ok) {
            generateButton.textContent = 'Generate';
            generateButton.disabled = false;

            const fileList = document.getElementById('fileList');
            while (fileList.firstChild) {
                fileList.removeChild(fileList.firstChild);
            }
            fetchFileList();
        } else {
            generateButton.textContent = 'Generate';
            generateButton.disabled = false;
            alert("Failed to generate HTML file. Please try again.");
        }
    })
    .catch(error => {
        generateButton.textContent = 'Generate';
        generateButton.disabled = false;
        console.error('Error generating HTML file:', error);
        alert("Failed to generate HTML file. Please try again.");
    });
}
