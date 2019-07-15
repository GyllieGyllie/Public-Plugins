$.when( $.ready ).then(function() {

    document.querySelectorAll(".required")
        .forEach(function (el) {
            el.addEventListener('input', function(event) {
                var allFilled = true;
                document.querySelectorAll(".required").forEach(function(el1) {
                    if (!el1.value || el1.value === "") {
                        allFilled = false;
                    }
                });

                if (allFilled) {
                    $("#paypal-button-container").css("display", "block");
                } else {
                    $("#paypal-button-container").css("display", "none");
                }
            });
        });

    paypal.Buttons({
        createOrder: function() {

            var errorDom = $("#alert");

            errorDom.html("");
            errorDom.css("display", "none");

            var username = $("#username")[0].value;
            var message = $("#message")[0].value;

            if (username.length > 50) {
                errorDom.html("Name cannot be longer than 50 characters!");
                errorDom.css("display", "block");
                throw "Failed to create payment";
            }

            var amount = 0;

            var amountDom = $("#amount");

            if (amountDom && amountDom[0]) {
                amount = Number(amountDom[0].value);

                if (amount < 1) {
                    errorDom.html("Please provide an amount of minimum $1");
                    errorDom.css("display", "block");
                    throw "Failed to create payment";
                }

                if (amount % 1 > 0) {
                    errorDom.html("Only round values allowed for the amount (for example $1, $5 not $1.5 or $5.5)");
                    errorDom.css("display", "block");
                    throw "Failed to create payment";
                }
            }

            var jqXHR = $.ajax({
                url: window.location.href,
                type: 'POST',
                data: {
                    name: username,
                    message: message,
                    amount: amount
                },
                async: false
            });

            if (jqXHR.status !== 200) {
                errorDom.html("Internal Error!");
                errorDom.css("display", "block");
                throw "Failed to create payment";
            }

            console.log(jqXHR);
            var json = JSON.parse(jqXHR.responseText);

            if (!json.success) {
                errorDom.html(json.message);
                errorDom.css("display", "block");
                throw "Failed to create payment: " + json.message;
            }

            return json.orderID;
        },
        onApprove: function(data) {
            $('.loading').css("display", "block");

            $.ajax({
                url: "/buy/approve",
                type: "POST",
                data: {
                    orderID: data.orderID
                },
                complete: function() {
                    window.location = "/payed/" + data.orderID;
                }
            });
        }
    }).render('#paypal-button-container');
});
