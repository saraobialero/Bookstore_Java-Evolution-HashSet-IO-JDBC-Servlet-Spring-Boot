package org.evpro.bookshopV5.controller;


import lombok.RequiredArgsConstructor;
import org.evpro.bookshopV5.service.LoanService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookshop/v5/loans")
public class LoanController {

    private final LoanService loanService;








}
