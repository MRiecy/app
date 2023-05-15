
window.setTimeout(function(){
$(".loading").fadeOut(500)
},400)

$(document).ready(function(){
	$(window).load(function () {
          $(".mobile-inner-header-icon").click(function(){
            $(this).toggleClass("mobile-inner-header-icon-click mobile-inner-header-icon-out");
            $(".mobile-inner-nav").slideToggle(250);
          });
          $(".mobile-inner-nav li").each(function( index ) {
            $( this ).css({'animation-delay': (index/10)+'s'});
          });
          $(".mobile-inner-nav li").click(function(){
            $(this).find('dl').slideToggle(200)
          })
        });

})

$(document).ready(function(){

$(".section_8 img").each(function( index ) {
            $( this ).css({'animation-delay': (index/10)+'s'});
          });

// var floating = new Swiper('.floating .swiper-container', {
//         pagination: '.floating .swiper-pagination',
//         paginationClickable: '.floating .swiper-pagination',
//         nextButton: '.floating .swiper-button-next',
//         prevButton: '.floating .swiper-button-prev',
//         autoplay:5000,
//         autoplayDisableOnInteraction: false,
//         speed:700,
//         slidesPerView: 1,
//         spaceBetween: 0
// });


  
  
  
  
$('.titlemodel').addClass('wow fadeInUp')





var wow = new WOW({
    boxClass: 'wow',
    animateClass: 'animated',
    offset: 0,
    mobile: true,
    live: true
});
wow.init();





});