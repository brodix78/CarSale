var customer;
var advert;
var advertsList;

//Customer download
function get_customer(func){
  customer = null;
  $.ajax({
    url: 'http://localhost:8080/CarSale/customer.do',
    type: 'GET',
    success:function(data) {
      customer = JSON.parse(data);
      func();
    }
  });
 }

// authorisation id=0, registration id=-500
   function auth(id){
     customer.id = id;
     customer.login = $("#login").val();
     customer.password = $("#pass").val();
     if (id == -500) {
       customer.phone = $("#phone").val();
       customer.name = $("#name").val();
     };
     $(
       $.ajax({
         url: 'http://localhost:8080/CarSale/customer.do',
         type: 'POST',
         data: JSON.stringify(customer)
       }).done(function(data) {
         user = JSON.parse(data);
         if (user.id == 0) {
           alert('Проверьте логин и пароль');
         } else if (user.id > 0){
           window.location.href = "index.html";
         }
       })
     );
   }

//adverts download if customer_id=0 then all adverts
function get_adverts(customer_id, start_advert, func) {
  adverts = null;
  let show_sold = document.getElementById('sold') == null ? true : sold.checked;
  let filter = {sold: show_sold,
           customerId: customer_id,
           firstAdvert: start_advert,
           maxSize: 20};
  $.ajax({
    url: 'http://localhost:8080/CarSale/advert.do?filter=' + encodeURIComponent(JSON.stringify(filter)),
    type: 'GET',
    success:function(data) {
      let adverts = JSON.parse(data);
      func(adverts);
    }
  });
}

//advert download by advert id
function get_advert(id, func) {
  $.ajax({
    url: 'http://localhost:8080/CarSale/advert.do?advertId=' + id,
    type: 'GET',
    success:function(data) {
      let advert = JSON.parse(data);
      func(advert);
    }
  });
}

//fill menu
function fill_menu(){
  while ((menu = document.getElementById("menu_content")) == null){};
  if (customer == null || customer.id < 1) {
    let elem = document.createElement('a')
    elem.setAttribute('href', 'auth.html');
    elem.setAttribute('class', 'dropdown-item');
    elem.appendChild(document.createTextNode('Войти'));
    menu.appendChild(elem);
    elem = document.createElement('a');
    elem.setAttribute('href', 'reg.html');
    elem.setAttribute('class', 'dropdown-item');
    elem.appendChild(document.createTextNode('Регистрация'));
    menu.appendChild(elem);
  } else {
    let elem = document.createElement('p');
    elem.setAttribute('class', 'dropdown-item');
    elem.appendChild(document.createTextNode(customer.login));
    menu.appendChild(elem);
    let line = document.createElement('div');
    line.setAttribute('class', 'dropdown-divider');
    menu.appendChild(line);
    elem = document.createElement('a');
    elem.setAttribute('href', 'advert_new.html');
    elem.setAttribute('class', 'dropdown-item');
    elem.appendChild(document.createTextNode('Добавить объявление'));
    menu.appendChild(elem);
    elem = document.createElement('a');
    elem.setAttribute('href', 'list_adverts.html');
    elem.setAttribute('class', 'dropdown-item');
    elem.appendChild(document.createTextNode('Мои объявления'));
    menu.appendChild(elem);
  }
}

//fill adverts for preview
function fill_adverts(adverts) {
  sorter(adverts, "date", false);
  let field = document.getElementById('adverts');
  field.innerHTML = "";
  for (let i = 0; adverts != null && i < adverts.length; i++) {
    let advert = adverts[i];
    let row = document.createElement('div');
    row.setAttribute('class', 'row')
      let col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
        let ref = document.createElement('a');
        ref.setAttribute('href', 'advert.html?advertId=' + advert.id);
          let image = document.createElement('img');
          image.setAttribute('src', 'http://localhost:8080/CarSale/image.do?file=' + advert.photos[0].file);
          image.setAttribute('width', '100px');
          image.setAttribute('height', '100px');
        ref.appendChild(image);
      col.appendChild(ref);
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-4');
      col.innerHTML = "<h4><strong>" + advert.config.generation.model.brand.name +
            " " + advert.config.generation.model.name + "</strong></h4>" +
            "<p>" + advert.config.body.name + ", " + advert.config.transmission.type + "</p>" +
            "<p>Двигатель: " + advert.config.engine.value + "л / " + advert.config.engine.HPower +
            "л.с. / " + advert.config.engine.fuel.type +"</p>";
    row.appendChild(col);
    let message = advert.sold ? "Продано </strong>" : advert.price + "</strong> руб.";
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5><stong>" + message + "</h5>";
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5>" + advert.prodYear + " год" + "</h5>";
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5>" + advert.mileage + " км" + "</h5>";
    row.appendChild(col);
    field.appendChild(row);
  };
};

//fill single advert for full view
function fill_advert(advert) {
  let config = advert.config;
  document.getElementById("brand").innerHTML = config.generation.model.brand.name;
  document.getElementById("model").innerHTML = config.generation.model.name;

  //put big image
  put_big_image(advert.photos[0].file)
  //put preview images
  document.getElementById("pr_photo").appendChild(prImages(6, advert.photos));
  // add notice
  document.getElementById("report").innerHTML = "<p>" + advert.report + "</p>";
  document.getElementById("car_data1").innerHTML =
          "<p>" + "Марка: " + config.generation.model.brand.name + "</p>" +
          "<p>" + "Модель: " + config.generation.model.name + "</p>" +
          "<p>" + "Поколение: " + config.generation.name + "</p>" +
          "<p>" + "Год выпуска: " + advert.prodYear + "</p>" +
          "<p>" + "Пробег: " + advert.mileage + "</p>";
  document.getElementById("car_data2").innerHTML =
          "<p>" + "Тип кузова: " + config.body.name + "</p>" +
          "<p>" + "Тип двигателя: " + config.engine.fuel.type + "</p>" +
          "<p>" + "Объем: " + config.engine.value + " л </p>" +
          "<p>" + "Мощность: " + config.engine.HPower + " л.с.</p>" +
          "<p>" + "Трансмиссия: " + config.transmission.type + "</p>";
  document.getElementById("price").appendChild(document.createTextNode(advert.price + " руб."));
};

//place big image to big_photo id element
function put_big_image(file) {
  let image = document.createElement('img');
  image.setAttribute('src', 'http://localhost:8080/CarSale/image.do?file=' + file);
  image.setAttribute('width', '450px');
  document.getElementById("big_photo").innerHTML = "";
  document.getElementById("big_photo").appendChild(image);
};

//create table for images preview with 'size' number of columns
function prImages(size, photos) {
  sorter(photos, "id", true);
  let table = document.createElement("table");
  let row;
  for (let i = 0; i < photos.length; i++) {
    if (i % size == 0) {
      if (i > 0) {
        table.appendChild(row);
      }
      row = document.createElement("tr");
    }
    let column = document.createElement('td');
    let button = document.createElement('button');
    button.setAttribute('onclick', 'put_big_image("' + photos[i].file + '");');
      let image = document.createElement('img');
      image.setAttribute('src', 'http://localhost:8080/CarSale/image.do?file=' + photos[i].file);
      image.setAttribute('width', '100px');
      image.setAttribute('height', '100px');
    button.appendChild(image);
    column.appendChild(button);
    row.appendChild(column);
  }
  table.appendChild(row);
  return table;
};

//fill adverts for customer edit
function edit_adverts(adverts) {
  advertsList = adverts;
  sorter(advertsList, "date", false);
  let field = document.getElementById('adverts');
  field.innerHTML = "";
  for (let i = 0; adverts != null && i < adverts.length; i++) {
    let advert = adverts[i];
    let row = document.createElement('div');
    row.setAttribute('class', 'row')
      let col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
        let ref = document.createElement('a');
        ref.setAttribute('href', 'advert.html?advertId=' + advert.id);
          let image = document.createElement('img');
          image.setAttribute('src', 'http://localhost:8080/CarSale/image.do?file=' + advert.photos[0].file);
          image.setAttribute('width', '100px');
          image.setAttribute('height', '100px');
        ref.appendChild(image);
      col.appendChild(ref);
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-3');
      col.innerHTML = "<h4><strong>" + advert.config.generation.model.brand.name +
            " " + advert.config.generation.model.name + "</strong></h4>" +
            "<p>" + advert.config.body.name + ", " + advert.config.transmission.type + "</p>" +
            "<p>Двигатель: " + advert.config.engine.value + "л / " + advert.config.engine.HPower +
            "л.с. / " + advert.config.engine.fuel.type +"</p>";
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5><stong>" + advert.price + "</strong> руб." + "</h5>";
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5>" + advert.prodYear + " год" + "</h5>";
    row.appendChild(col);
      col = document.createElement('div');
      col.setAttribute('class', 'col-lg-2');
      col.innerHTML = "<h5>" + advert.mileage + " км" + "</h5>";
    row.appendChild(col);
      col = document.createElement('div');
        col.setAttribute('class', 'col-lg-1');
        if (advert.sold == false) {
          let button = document.createElement('button');
          button.setAttribute('type', 'button');
          button.setAttribute('class', 'btn btn-primary btn-sm');
          button.setAttribute('onclick', 'advertsList[' + i + '].sold=true;post_advert(advertsList[' + i + '], customer_adverts)');
          button.appendChild(document.createTextNode("Продано"));
          col.appendChild(button);
        } else {
          col.appendChild(document.createTextNode("Закрыто"));
        }
    row.appendChild(col);
    field.appendChild(row);
  };
};

//update advert
function post_advert(advert, func){
  let yes = confirm("Подтвердите, что автомобиль продан и вы снимаете объявление с публикаци");
  if (yes) {
    $.ajax({
      url: 'http://localhost:8080/CarSale/advert.do',
      type: 'POST',
      data: JSON.stringify(advert)
    }).done(function(data) {
      console.log(data);
      func();
    })
  }
};

//fill_menu
function menu() {
  get_customer(fill_menu);
};

//Show adverts preview
function adverts(){
  get_adverts(0, 0, fill_adverts);
};

//show customers adverts for edit
function customer_adverts(){
  if (customer == undefined) {
    $.when(
      $.ajax({
        url: 'http://localhost:8080/CarSale/customer.do',
        type: 'GET',
        success:function(data) {
          customer = JSON.parse(data);
        }
      })
    ).then(function() {
      get_adverts(customer.id, 0, edit_adverts);
    });
  } else {
    get_adverts(customer.id, 0, edit_adverts);
  }
};

//show singlr advert by id
function advert(id){
  get_advert(id, fill_advert);
};

//show customers phone
function show_phone() {
  document.getElementById("phone").innerHTML = customer.phone;
};

//Array sorter by key
function sorter(object, key, asc) {
  object.sort(function (a, b){
    if (asc) {
      return a[key] - b[key];
    } else {
      return b[key] - a[key];
    }

  })
}
