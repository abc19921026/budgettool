var Login = function() {

    var handleLogin = function() {

        $('.login-form').validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                username: {
                    required: true
                },
                password: {
                    required: true
                },
                remember: {
                    required: false
                }
            },

            messages: {
                username: {
                    required: "Username is required."
                },
                password: {
                    required: "Password is required."
                }
            },

            invalidHandler: function(event, validator) { //display error alert on form submit   
                $('.alert-danger', $('.login-form')).show();
            },

            highlight: function(element) { // hightlight error inputs
                $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            success: function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function(error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler: function(form) {
                form.submit(); // form validation success, call ajax form submit
            }
        });

        $('.login-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('.login-form').validate().form()) {
                    $('.login-form').submit(); //form validation success, call ajax form submit
                }
                return false;
            }
        });

        $('.forget-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('.forget-form').validate().form()) {
                    $('.forget-form').submit();
                }
                return false;
            }
        });

        $('#forget-password').click(function(){
            $('.login-form').hide();
            $('.forget-form').show();
        });

        $('#back-btn').click(function(){
            $('.login-form').show();
            $('.forget-form').hide();
        });
    }

 
  

    return {
        //main function to initiate the module
        init: function() {

            handleLogin();

            // init background slide images
            var rand = Math.floor((Math.random() * 10));//Return a random number between 0 and 9
            //随机挑选一张当背景图
            var image_path = "/public/img/app/login_bg" + rand +"0.jpg";
            console.log(image_path);
            $('.login-bg').backstretch([
                image_path
                /*"/public/img/app/login_bg00.jpg",
                "/public/img/app/login_bg10.jpg",
                "/public/img/app/login_bg20.jpg",
                "/public/img/app//login_bg30.jpg",
                "/public/img/app//login_bg40.jpg",
                "/public/img/app//login_bg50.jpg",
                "/public/img/app//login_bg60.jpg",
                "/public/img/app//login_bg70.jpg",
                "/public/img/app//login_bg80.jpg",
                "/public/img/app//login_bg90.jpg",*/
                ], {
                  fade: 1000,
                  duration: 6000
                }
            );

            $('.forget-form').hide();

        }

    };

}();

jQuery(document).ready(function() {
    Login.init();
    
    $("body").keypress(function(event){//enter也能登录
        //var keycode = (event.keyCode ? event.keyCode : event.which);  
        var keycode = event.keyCode || event.which;
        if(keycode == 13){//13代表enter
        	$("#login").click();
        }  
    });
});