package ru.unit_techno.car_entry_control.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
@AllArgsConstructor
public class EventController {

  @PostMapping("/event")
  @ResponseStatus(HttpStatus.OK)
  public String eventHandler(String rfidLabel) {
    return "S1\r\nIN\r\n";
  }

}
