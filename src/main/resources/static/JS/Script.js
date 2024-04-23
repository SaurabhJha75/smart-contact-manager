console.log("This is Java Script file");

const toggleSidebar = () => {
	if ($('.sidebar').is(":visible")) {
		// true
		// band karna hai
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		// false
		// show karna hai
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

const search = () => {
	//console.log("searching....");

	let query = $("#search-input").val();

	if(query == ""){
		$(".search-result").hide();
	}else{
		//search
		//console.log(query);

		//sending request to server
		let url = `http://localhost:9090/search/${query}`;

		//fetching
		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				//data
				//console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach(contact => {
					text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});

		
	}
};

/* Function to open the slide bar */
    function openNav() {
        document.getElementById("mySidebar").style.width = "250px";
        document.getElementById("main").style.marginLeft = "250px";
    }

    /* Function to close the slide bar */
    function closeNav() {
        document.getElementById("mySidebar").style.width = "0";
        document.getElementById("main").style.marginLeft= "0";
    }
