function updateDateDayTime() {
  const currentDate = new Date();
  const options = {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
    second: "numeric",
    hour24: true,
  };
  const formattedDate = currentDate.toLocaleDateString("en-US", options);

  document.getElementById("dateDayTime").innerText = formattedDate;
}

window.onload = function () {
  updateDateDayTime();

  setInterval(updateDateDayTime, 1000);
};
