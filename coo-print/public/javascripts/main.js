$(function () {
    'use strict';
    $('#fileupload').fileupload({
        url: 'upload',
        dataType: 'json',
        add: function (e, data) {
        	$('#main_div').css('display', 'none');
        	$('#fileupload_div').css('display', 'initial');
        	
        	data.context = $('<div class="progress"> \
        			<div class="progress-bar progress-bar-success"> \
        			<span style="margin-left:20px;">'+data.files[0].name+'</span></div> \
        			<div class="progress-indicator"></div></div>').appendTo('#files');
        	data.submit();
        },
        done: function (e, data) {
       	
        	var shoppingBtn = $('#gotoShoppingBtn');
        	if (shoppingBtn.length > 0) {
        		shoppingBtn.css('visibility','visible');
        	} else {
        		$('#files').hide('slow',  function() {
        		    $( this ).children().remove();
        		  });
        	}
        	
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
    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');
    
    $('#login_form').submit(function(event) {
    	event.preventDefault();
    	var that = this;
    	$.ajax({
            type: "POST",
            url: "/login",
            data: $(that).serialize(), 
            success: function(data)
            {
            	if (data.status == 'ok') {
            		$(that).find(".error").toggleClass('hidden', true);
                	$('#modal_login').find('.close-modal').click();
                	$('.navbar-login').hide();
                	$('.navbar-logout').find('.navbar-text').text(data.msg);
                	$('.navbar-logout').toggleClass('hidden');	
            	} else {
            		$(that).find(".error").text(data.msg);
            		$(that).find(".error").toggleClass('hidden', false);
            	}
            	
                
            }
        });
   
	});
});