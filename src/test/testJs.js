$(document).ready(function() {
    $("a").click(function(event) {
        alert("hi");
        event.preventDefault();
        $(this).hide("slow");
    });

    $("a").addClass("test");
});