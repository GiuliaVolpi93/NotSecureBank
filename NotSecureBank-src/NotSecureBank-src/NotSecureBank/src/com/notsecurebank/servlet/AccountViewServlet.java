package com.notsecurebank.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AccountViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(AccountViewServlet.class);

    public AccountViewServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.info("doGet");

        // show account balance for a particular account
        if (request.getRequestURL().toString().endsWith("showAccount")) {
            String accountName = request.getParameter("listAccounts");
            if (accountName == null) {
                response.sendRedirect(request.getContextPath() + "/bank/main.jsp");
                return;
            }
            LOG.info("Balance for accountName = '" + accountName + "'.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/balance.jsp?acctId=" + accountName);
            dispatcher.forward(request, response);
            return;
        }
        // this shouldn't happen
        else if (request.getRequestURL().toString().endsWith("showTransactions"))
            doPost(request, response);
        else
            super.doGet(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.info("doPost");

        if (request.getRequestURL().toString().endsWith("showTransactions")) {
            String startTime = request.getParameter("startDate");
            String endTime = request.getParameter("endDate");

            try {
                if (startTime != null && endTime != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    LocalDate startDate = LocalDate.parse(startTime);
                    LocalDate endDate = LocalDate.parse(endTime);

                    // Check if endDate is after startDate
                    if (endDate.isBefore(startDate)) {
                        endDate = startDate;
                    }

                    String formattedStartDate = startDate.format(formatter);
                    String formattedEndDate = endDate.format(formatter);

                    LOG.info("Transactions within '" + formattedStartDate + "' and '" + formattedEndDate + "'.");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/bank/transaction.jsp?" +
                            "&startTime=" + formattedStartDate +
                            "&endTime=" + formattedEndDate);
                    dispatcher.forward(request, response);
                }
            } catch (DateTimeParseException e) {
                LOG.error("Error parsing date: " + e.getMessage());
            }
        }
    }
}
