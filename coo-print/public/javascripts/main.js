$(function () {
    'use strict';
    $('#fileupload').fileupload({
        url: '/upload',
        dataType: 'json',
        limitConcurrentUploads: 1,
        sequentialUploads: true,
        add: function (e, data) {
                $('#main_div').css('display', 'none');
                $('#fileupload_div').css('display', 'initial');
                
                data.context = $('<div class="progress"> \
                                <div class="progress-bar progress-bar-success"> \
                                <span style="margin-left:20px;">'+data.files[0].name+'</span></div> \
                                <div class="progress-indicator"></div></div>').appendTo('#files');
                data.submit();
        },
	getFilesFromResponse: function (data) {
                if (data.result && $.isArray(data.result.files)) {
                    return data.result.files;
                }
                return [];
        },
        done: function (e, data) {
        
                var shoppingBtn = $('#gotoShoppingBtn');
                if (shoppingBtn.length > 0) {
                        shoppingBtn.css('visibility','visible');
                } else {
                        $('#files').hide(1000,  function() {
                            $( this ).children().remove();
			    var file = data.result.files[0];
                        $.ajax({
                                type:"GET",
                                url:"/s/item/"+file.name,
                                success:function(data2) {
                                    var newItem = $(data2);
                                    newItem.appendTo('#item_form');
                                    
                                    var oic = Number($('.object-count').text()) + Number(newItem.find('.item-quantity').text())
                                    $('.object-count').text(oic);
                                    
                                    var orderPrice = 0;
                                    $('.item-total-price').each(function(idx, val){
                                        orderPrice += Number($(val).text());
                                    });
                                    $('.order-price').text(orderPrice.toFixed(2));
                                    $('.total-price').text(orderPrice.toFixed(2));                                    
                                }
                        });
                        $( this ).show();
                        $('#shopping-step-item').removeAttr('disabled');
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
    
    $('select[name$=".material"]').change(function(event){
        var parent = $(this).closest(".order-item");
        
        var mOpt = $(this).find('option:selected');
        parent.find('.item-material').text(mOpt.data('name'));
        parent.find('.item-price').text(mOpt.data('price'));
        
        var cnt = parent.find('select[name$=".quantity"]').find('option:selected').text()
        var totalPrice = Number(cnt) * Number(mOpt.data('price'));
        parent.find('.item-total-price').text(totalPrice.toFixed(2));
        
        var orderPrice = 0;
        $('.item-total-price').each(function(idx, val){
                orderPrice += Number($(val).text());
        });
        $('.order-price').text(orderPrice.toFixed(2));
        $('.total-price').text(orderPrice.toFixed(2));
        
                
                var color = parent.find("select[name$='.color']");              
                var finish = parent.find("select[name$='.finish']");                            
                var fill = parent.find("select[name$='.fill']");                
                var layer = parent.find("select[name$='.layer']");              
                var zoom = parent.find("select[name$='.zoom']");

        $.ajax({
                type:"GET",
                url:"/s/props/"+$(this).val(),
                success:function(data) {
                    color.children().remove();
                    $.each(data.color, function(i, c){
                        if (i == 0) { $('<option selected>' + c +'</option>').appendTo(color); }
                        else {$('<option>' + c +'</option>').appendTo(color)};
                    });
                    finish.children().remove();
                    $.each(data.finish, function(i, c){
                        $('<option>' + c +'</option>').appendTo(finish);
                    });
                    fill.children().remove();
                    $.each(data.fill, function(i, c){
                        $('<option>' + c +'</option>').appendTo(fill);
                    });
                    layer.children().remove();
                    $.each(data.layer, function(i, c){
                        $('<option>' + c +'</option>').appendTo(layer);
                    });
                    zoom.children().remove();
                    $.each(data.zoom, function(i, c){
                        $('<option>' + c +'</option>').appendTo(zoom);
                    });
                    
                    parent.find('.item-color').text(color.find('option:selected').text());
                    parent.find('.item-finish').text(finish.find('option:selected').text());
                }

        })
    });
    
    $('select[name$=".quantity"]').change(function(event){

        var parent = $(this).closest('.order-item');
        
        var cnt = $(this).find('option:selected').text();
        parent.find('.item-quantity').text(cnt);
        
        var price = parent.find('select[name$=".material"]').find('option:selected').data('price');
        var totalPrice = Number(cnt) * Number(price);
        
        parent.find('.item-total-price').text(totalPrice.toFixed(2));
        
        var orderPrice = 0;
        $('.item-total-price').each(function(idx, val){
                orderPrice += Number($(val).text());
        });
        $('.order-price').text(orderPrice.toFixed(2));
        $('.total-price').text(orderPrice.toFixed(2));

	var objCnt = 0;
        $('.item-quantity').each(function(idx, val){
                objCnt += Number($(val).text());
        });
	$('.object-count').text(objCnt);
        
    });
    
    
    $('select[name$=".color"]').change(function(event){

        var parent = $(this).closest('.order-item');    
        parent.find('.item-color').text($(this).find('option:selected').text());

    });
    
    $('select[name$=".finish"]').change(function(event){

        var parent = $(this).closest('.order-item');    
        parent.find('.item-finish').text($(this).find('option:selected').text());

    });
    
    $('#delConfirmModal').on('show.bs.modal', function (event) {
        var target = $(event.relatedTarget)
        
        var orderItem = $('#'+target.data('order-item'));
        var fileName = target.data('file-name');
        
        var modal = $(this);
        modal.find('.item-name').text(fileName);
        
        var btnPrimary = modal.find('.btn-primary');
        btnPrimary.off('click');
        btnPrimary.click(function(event) {
                $.ajax({
                        type:"DELETE",
                        url:"/s/item/"+fileName,
                        success:function(data) {
                                
                                modal.find('.btn-default').click();
                        
                            orderItem.hide('slow',function(){
                                
                                var oic = Number($('.object-count').text()) - Number(orderItem.find('.item-quantity').text())
                                $('.object-count').text(oic);
                                if (oic <= 0) {
                                        $('#shopping-step-item').attr('disabled', true);
                                }
                                
                                var price = (Number($('.order-price').text()) - Number($(this).find('.item-total-price').text())).toFixed(2);
                                $('.order-price').text(price);
                                $('.total-price').text(price);
                                
                                
                                $(this).remove();
                        });
                        
                        }
                });
        });
        
    });
    
    $(document).on('click.bs.dropdown.data-api', '.self-show', function (e) {
        e.stopPropagation() 
    });

    $('ul.dropdown-menu[aria-labelledby^="dropdownMenu_post_"] > li > a').click(function(event){
        var a = $(this);
        var radio = a.closest('.radio').find('input:radio[name="post_method"]');
        radio.siblings("span").text(a.text());
        radio.val(a.data("post-method"));
        if (radio.prop("checked")) {
            $("#post_method").val(a.data("post-method"));
        }

    });
    $('input:radio[name="post_method"]').click(function(event){
	var v = $(this).val();
        $("#post_method").val(v);
	var c = (v == "self")
        $('#fc_control').prop('disabled', c);
	
	if(c) $('#fc_control').prop('checked', false);
    });
        
    
});
