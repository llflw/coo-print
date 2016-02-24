$(function () {
    'use strict';
    $('#fileupload').fileupload({
        url: 'upload',
        dataType: 'json',
        add: function (e, data) {
        	$('#main_div').css('display', 'none');
        	$('#fileupload_div').css('display', 'block');
        	
        	data.context = $('<div class="progress"> \
        			<div class="progress-bar progress-bar-success"> \
        			<span style="margin-left:20px;">'+data.files[0].name+'</span></div> \
        			<div class="progress-indicator">123</div></div>').appendTo('#files');
        	data.submit();
        },
        done: function (e, data) {
        	/*
            $.each(data.result.files, function (index, file) {
                $('<p/>').text(file.name).appendTo('#files');
            });\*/
        },
        
        progress: function (e, data) {
            var progress = parseInt(data._progress.loaded / data._progress.total * 100, 10);
            data.context.each(function() {
            	$(this).find('.progress-bar').css(
                        'width',
                        progress + '%'
                );	
            	$(this).find('.progress-indicator').text(progress + '%');
            });


        },
        /*
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
                'width',
                progress + '%'
            );
        }*/
    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');
});