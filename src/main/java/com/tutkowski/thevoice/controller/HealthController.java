package com.tutkowski.thevoice.controller;

public class HealthController {
    public void init() {
        spark.Spark.get("/health", (req, res) -> {
            res.status(200);
            return "OK";
        });
    }
}