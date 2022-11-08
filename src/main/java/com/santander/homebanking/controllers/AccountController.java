package com.santander.homebanking.controllers;

import com.santander.homebanking.dtos.AccountDTO;
import com.santander.homebanking.models.Client;
import com.santander.homebanking.repositories.AccountRepository;
import com.santander.homebanking.repositories.ClientRepository;
import com.santander.homebanking.services.AccountService;
import com.santander.homebanking.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeParseException;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MessageSource messages;


    @GetMapping(value = "/accounts")
    public Set<AccountDTO> getAccounts(){
        return accountService.getAccounts();
    }

    @GetMapping(value = "/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountService.getAccount(id);
    }

    @GetMapping(value = "/accounts/number/{number}")
    public AccountDTO getAccountByNumber(@PathVariable String number){
        return accountService.getAccountByNumber(number);
    }

    @GetMapping(value = "/accounts/creation-date/{creationDate}")
    public Set<AccountDTO> getAccountByCreationDate(@PathVariable String creationDate){
        try {
            return accountService.getAccountByCreationDate(creationDate);
        } catch (DateTimeParseException e){
            return null;
        }
    }

    @GetMapping(value = "/accounts/balance/greater/{balance}")
    public Set<AccountDTO> getAccountByBalanceGreaterThan(@PathVariable Long balance){
        return accountService.getAccountByBalanceGreaterThan(balance);
    }

    @GetMapping(value = "/accounts/balance/{balance}")
    public Set<AccountDTO> getAccountByBalance(@PathVariable Long balance){
        return accountService.getAccountByBalance(balance);
    }

    @GetMapping(value = "/accounts/number-inj/{number}")
    public Set<AccountDTO> getAccountByNumberInj(@PathVariable String number){
        return accountService.getAccountByNumberInj(number);
    }

    @GetMapping(value = "/clients/current/accounts/{id}")
    public AccountDTO getAccountByIdCurrentClient(@PathVariable Long id, HttpSession session){
        return accountService.getAccountByIdCurrentClient(id, (Client) session.getAttribute("client"));
    }


    @GetMapping(value = "/clients/current/accounts")
    public Set<AccountDTO> getCurrentAccounts(HttpSession session){
        return accountService.getCurrentAccounts(session);
    }

    @PostMapping(value = "/clients/current/accounts")
    public ResponseEntity<Object> addAccount(HttpSession session){
        ResponseUtils res = accountService.addAccount((Client) session.getAttribute("client"));

        if (res.getDone()){
            return new ResponseEntity<>(messages.getMessage(
                    res.getMessage(), null, LocaleContextHolder.getLocale()),
                    HttpStatus.valueOf(res.getStatusCode()));
        }

        return new ResponseEntity<>(messages.getMessage(
                String.valueOf(res.getMessage()), res.getArgs(), LocaleContextHolder.getLocale()),
                HttpStatus.valueOf(res.getStatusCode()));
    }
}
