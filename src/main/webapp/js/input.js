function input_alarm(target, al_message){
  let alarm = document.getElementById(target + "_alarm");
  if (alarm == null) {
    let label = document.getElementById(target + "_label");
    label.setAttribute("for", "inputDanger1");
    label.setAttribute("class", "form-control-label");
    let tar = document.getElementById(target);
    tar.setAttribute("class", "form-control is-invalid");
    alarm = document.createElement("div");
    alarm.setAttribute("class", "invalid-feedback");
    alarm.setAttribute("id", target + "_alarm");
    alarm.innerHTML = al_message;
    let input = document.getElementById(target + "_input");
    input.appendChild(alarm);
  }
};
function input_default(target){
  let alarm = document.getElementById(target + "_alarm");
  if (alarm != null) {
    let label = document.getElementById(target + "_label");
    label.setAttribute("for", "inputDefault");
    label.setAttribute("class", "col-form-label");
    let tar = document.getElementById(target);
    tar.setAttribute("class", "form-control");
    let input = document.getElementById(target + "_input");
    input.removeChild(alarm);
  }
};
function clear_element(elementId){
   document.getElementById(elementId).innerHTML = '';
   document.getElementById(elementId).val = null;
};
