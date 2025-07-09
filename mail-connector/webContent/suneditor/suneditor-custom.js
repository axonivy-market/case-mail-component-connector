function initSunEditor(elementId, iframe, width, height, resizeEnable, defaultFontSize, defaultFontColor, applyDefaultStyle = true) {

	const baseConfig = {
		buttonList: [
			['font', 'fontSize', 'formatBlock'],
			['bold', 'underline', 'italic'],
			['fontColor', 'hiliteColor', 'outdent', 'indent', 'align', 'horizontalRule', 'list', 'table'],
			['fullScreen'],
			['undo', 'redo'],
		],
		defaultStyle: 'font-family: Arial; color: '+ defaultFontColor + '; font-size: ' + defaultFontSize + 'px;',
		attributesWhitelist: {
            'all': 'width|height|role|border|cellspacing|cellpadding|src|alt|href|target|style|data-.+'
        },
        iframe: iframe,
        width : width,
        height : height,
        resizeEnable : resizeEnable,
        stickyToolbar : -1,
	};
	
	if(!applyDefaultStyle) {
		Object.assign(baseConfig, {
			allowClassName: (className) => true,
			pasteFilter: false,
			pasteTagsWhitelist: 'style|class|id|div|span|p|br|strong|em|u|b|i|ul|ol|li|table|tr|td|th|thead|tbody|tfoot|a|img|h1|h2|h3|h4|h5|h6',
			fullPage: true,
			codeViewFilter: false,
			codeViewIframeFilter: false,
			iframeAttributes: { style: 'font-family: Arial; color: '+ defaultFontColor + '; font-size: ' + defaultFontSize + 'px;'}
		});
		baseConfig.attributesWhitelist.all += '|class|id';
	}
	
	sunEditor = SUNEDITOR.create(document.getElementById(elementId), baseConfig);
	
    sunEditor.onChange = (contents, core) => {
        core.functions.save();
    };
}

let sunEditor; 

//workaround to force sunEditor apply style when the template is loaded the first time and no need to manipulate it (CMTD-1022)
function forceContentReload() {
	if (!sunEditor) return;
	
    const currentContent = sunEditor.getContents();
	if (currentContent == null) {
    	return;
	}	
    sunEditor.setContents(currentContent + ' ');
    sunEditor.setContents(sunEditor.getContents().trim());
}