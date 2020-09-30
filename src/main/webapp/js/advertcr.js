var images = [];
var generation;

function get_obj(type, id, func){
  let rsl = null;
  $.ajax({
    url: 'http://localhost:8080/CarSale/car.do?reqId=' + id + '&get=' + type,
    type: 'GET',
    success:function(data) {
      let dt = JSON.parse(data);
      if (func != null) {
        func(dt);
      }
    }
  });
};

function brands() {
  get_obj("brand", -100, op_brands);
}

function post_advert(){
  if ($("#config").val() >= 0 && $("#year").val() > 0
  && $("#mileage").val() > 0 && $("#price").val() > 0) {
    let advert = {id: 0,
                  customer: customer,
                  report: $("#report").val(),
                  price: parseInt($("#price").val()),
                  date: Date.now(),
                  mileage: parseInt($("#mileage").val()),
                  prodYear: parseInt($("#year").val()),
                  config: generation.configs[$("#config").val()],
                  photos: images,
                  sold: false};
    $.ajax({
      url: 'http://localhost:8080/CarSale/advert.do',
      type: 'POST',
      data: JSON.stringify(advert)
    }).done(function(data) {
      let adv = JSON.parse(data);
      if (adv.id > 0) {
        alert("Объявление #" + adv.id + " успешно размещено");
        window.location.href = "index.html";
      } else {
        alert("Что-то пошло не так, пожалуйста обновите страницу и попробуйте снова");
      }
    })
  } else {
    alert("Заполните все поля");
  }
};

function op_brands(brands) {
  sorter(brands, "name", true);
  let element = document.getElementById('brand');
  for (let i = 0; i < brands.length; i++) {
    let option = document.createElement('option');
    option.setAttribute('onclick', "get_obj('brand', " + brands[i].id + ", fill_model)");
    option.appendChild(document.createTextNode(brands[i].name));
    element.appendChild(option);
  }
};

function fill_model(brand) {
  clear_element("model");
  clear_element("generation");
  clear_element("config");
  clear_element("year");
  sorter(brand.models, "name", true);
  let element = document.getElementById('model');
  for (let i = 0; i < brand.models.length; i++) {
    let option = document.createElement('option');
    option.setAttribute('name', 'model');
    option.setAttribute('onclick', "get_obj('model', " + brand.models[i].id + ", fill_gen)");
    option.appendChild(document.createTextNode(brand.models[i].name));
    element.appendChild(option);
  }
};

function fill_gen(model) {
  clear_element("generation");
  clear_element("config");
  clear_element("year");
  sorter(model.generations, "name", true);
  let element = document.getElementById('generation');
  for (let i = 0; i < model.generations.length; i++) {
    let option = document.createElement('option');
    let gen = model.generations[i];
    option.setAttribute('name', 'generation');
    option.setAttribute('onclick',"get_obj('generation', " + gen.id + ", fill_config)");
    option.appendChild(document.createTextNode(gen.name + ' ' + gen.startYear + '-' + gen.endYear));
    element.appendChild(option);
  }
};

function fill_config(gen) {
  generation = gen;
  clear_element("config");
  clear_element("year");
  sorter(generation.configs, "body.name", true);
  let element = document.getElementById('config');
    for (let i = 0; i < generation.configs.length; i++) {
    let option = document.createElement('option');
    let config = generation.configs[i]
    option.setAttribute('name', 'config');
    option.setAttribute('value', i);
    let engine = config.engine;
    let configDescr = config.body.name + ", " + engine.value + "л " +
      engine.fuel.type + " " + engine.HPower + "л.с., " + config.transmission.type;
    option.appendChild(document.createTextNode(configDescr));
    element.appendChild(option);
  }
  element = document.getElementById('year');
  for (let i = generation.startYear; i <= generation.endYear; i++) {
    let option = document.createElement('option');
    option.setAttribute('name', 'year');
    option.setAttribute('value', i);
    option.appendChild(document.createTextNode(i));
    element.appendChild(option);
  }
};

function upload(){
  var data = new FormData();
  $.each(files, function(key, value){
    data.append(key, value);
  });
  $.ajax({
    url: 'http://localhost:8080/CarSale/image.do',
    type: 'POST',
    data: data,
    cache:false,
    contentType: false,
    processData: false,
    success:function(data) {
      let imgs = JSON.parse(data);
      for (let i = 0; i < imgs.length && images.length < 6; i++) {
        image = JSON.parse(data)[i];
        images[images.length] = image;
        addToPrev(6, image);
      };
    }
  })
};

function addToPrev(size, photo) {
  let row = document.getElementById('pr_photo');
  let column = document.createElement('td');
  let image = document.createElement('img');
    image.setAttribute('src', 'http://localhost:8080/CarSale/image.do?file=' + photo.file);
    image.setAttribute('width', '100px');
    image.setAttribute('height', '100px');
  column.appendChild(image);
  row.appendChild(column);
};

function clear_element(name) {
  var element = document.getElementsByName(name);
  for (index = element.length - 1; index >= 0; index--) {
    element[index].parentNode.removeChild(element[index]);
  }
};
