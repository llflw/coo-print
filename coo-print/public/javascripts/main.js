$(function () {
    'use strict';
    $('#fileupload').fileupload({
        url: 'upload',
        dataType: 'json',
        add: function (e, data) {
            var progressBar = $('<div class="progress"></div>');            
            progressBar.appendTo('#files');
            progressBar.append($('<div class="progress-bar progress-bar-success"></div>').text(data.files[0].name));
        	data.context = progressBar;
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