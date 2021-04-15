const anchorForId = function (id) {
    const anchor = document.createElement("a");
    anchor.className = "anchor";
    anchor.href = "#" + id;
    anchor.innerHTML = "<span class=\"octicon octicon-link\"></span></a>";
    return anchor;
};

const linkifyAnchors = function (level, containingElement) {
    const headers = containingElement.getElementsByTagName("h" + level);
    for (let h = 0; h < headers.length; h++) {
        const header = headers[h];

        if (typeof header.id !== "undefined" && header.id !== "") {
            header.insertBefore(anchorForId(header.id), header.firstChild);
        }
    }
};

document.onreadystatechange = function () {
    if (this.readyState === "complete") {
        const contentBlock = document.getElementsByClassName("markdown-body")[0];
        if (!contentBlock) {
            return;
        }
        for (let level = 1; level <= 6; level++) {
            linkifyAnchors(level, contentBlock);
        }
    }
};
