package com.seven.tech.app.controller;

import com.seven.tech.app.service.BillingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingAccountController {

    @Autowired
    BillingAccountService accountService;

    @GetMapping(value = "/transfer")
    public ResponseEntity<Boolean> transferFunds(@RequestParam long from,
                                                 @RequestParam long to,
                                                 @RequestParam float sum) {
        return new ResponseEntity<Boolean>(accountService.transferFunds(from, to, sum), HttpStatus.OK);
    }

    @GetMapping(value = "/populate/{billingAccountId}")
    public ResponseEntity<Boolean> addFunds(@PathVariable long billingAccountId, @RequestParam long sum) {
        return new ResponseEntity<Boolean>(accountService.addFunds(billingAccountId, sum), HttpStatus.OK);
    }

    @GetMapping(value = "/withdraw/{billingAccountId}")
    public ResponseEntity<Boolean> withdrawFunds(@PathVariable long billingAccountId, @RequestParam long sum) {
        return new ResponseEntity<Boolean>(accountService.withdrawFunds(billingAccountId, sum), HttpStatus.OK);
    }
}
