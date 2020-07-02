package com.seven.tech.app.service;

import com.seven.tech.app.model.BillingAccount;
import com.seven.tech.app.repository.BillingAccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@Transactional
public class BillingAccountService {

    @Autowired
    BillingAccountRepository accountRepository;

    private final static Logger LOG = LogManager.getLogger(BillingAccountService.class);


    private Optional<BillingAccount> getById(long id) {
        return accountRepository.findById(id);
    }

    private boolean isWithdrawPossible(BillingAccount account, float sum) {
        return BigDecimal.valueOf(account.getAmount()).setScale(2, RoundingMode.HALF_UP)
                .subtract(BigDecimal.valueOf(sum)).compareTo(BigDecimal.ZERO) > 0;
    }

    public synchronized boolean transferFunds(long from, long to, float sum) {
        return getById(from)
                .map(fromAccount -> getById(to)
                        .map(toAccount -> {
                            boolean result = false;
                            if (isWithdrawPossible(fromAccount, sum)) {
                                result = withdrawFunds(fromAccount, sum);
                                addFunds(toAccount, sum);
                            }
                            return result;
                        }).orElseGet(() -> false)).orElseGet(() -> false);
    }

    public boolean addFunds(long id, float sum){
        return getById(id)
                .map(account -> {
                    addFunds(account, sum);
                    return true;
                })
                .orElseGet(() -> false);
    }

    private void addFunds(BillingAccount account, float sum) {
        account.setAmount(BigDecimal.valueOf(account.getAmount()).setScale(2, RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(sum)).floatValue());
    }

    public boolean withdrawFunds(long id, float sum) {
        return getById(id)
                .map(account -> withdrawFunds(account, sum))
                .orElseGet(() -> false);
    }

    private synchronized boolean withdrawFunds(BillingAccount account, float sum) {
        boolean result = false;
        if (isWithdrawPossible(account, sum)) {
            account.setAmount(BigDecimal.valueOf(account.getAmount())
                    .setScale(2, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.valueOf(sum)).floatValue());
            result = true;
        } else {
            LOG.debug(String.format("Account with id %s doesn't have enough funds", account.getId()));
        }
        return result;
    }

}
