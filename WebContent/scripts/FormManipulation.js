/**
 * JavaScript methods for form manipulation 
 */

/**
 * selectMultiple
 * 
 * Select all/no checkboxes in html form
 * 
 * @param noneOrAll REQUIRED boolean false: none, true: all;
 * @returns
 */
function selectMultiple(noneOrAll, fieldName, fieldValue) {
	let forms = document.getElementsByTagName('form');
	if (!forms || !forms.length) {
		return;
	}
	
	let elements = forms[0].elements;
	Array.from(elements).forEach(element => {
		if (fieldName && fieldValue && element.getAttribute &&
				(!element.getAttribute(fieldName) ||
				 (element.getAttribute(fieldName) !== fieldValue))) {
			return;
		}
		if (element && element.tagName && 
				element.tagName=='INPUT' && 
				element.getAttribute('type').toUpperCase()=='CHECKBOX') {
			element.checked = !!noneOrAll;
		}
	});
}