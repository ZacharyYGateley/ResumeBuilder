/**
 * General cleanup javascript
 */

function cleanupResumeStyle() {
	fixEducationWidths();
}

function fixEducationWidths() {
	let col1 = document.getElementsByClassName("Education1");
	let col2 = document.getElementsByClassName("Education2");
	let col3 = document.getElementsByClassName("Education3");
	let col4 = document.getElementsByClassName("Education4");
	
	// Get max width
	let col1Width = 0;
	let col2Width = 0;
	for (let i = 0; i < col1.length; i++) {
		// Column 1
		let col = col1[i];
		let style = window.getComputedStyle(col);
		// In pixels
		let width = parseInt(style.getPropertyValue("width"));
		if (width > col1Width) {
			col1Width = width;
		}
		
		// Column 2
		// # Col1 >= # Col2 (Col1 always exists)
		if (i < col2.length) {
			col = col2[i];
			style = window.getComputedStyle(col);
			// In pixels
			width = parseInt(style.getPropertyValue("width"));
			if (width > col2Width) {
				col2Width = width;
			}
		}
	}
	
	// Set widths to max width
	for (let i = 0; i < col1.length; i++) {
		let col = col1[i];
		col.style.width = col1Width + "px";

		// Column 2
		// # Col1 >= # Col2 (Col1 always exists)
		if (i < col2.length) {
			col = col2[i];
			col.style.width = col2Width + "px";
		}
	}
}