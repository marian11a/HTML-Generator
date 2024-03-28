function openHTMLFileInNewTab(fileName) {
    const url = `/open-html/${fileName}`;
    window.open(url, '_blank');
}