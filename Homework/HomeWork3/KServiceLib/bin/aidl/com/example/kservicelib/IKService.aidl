package com.example.kservicelib;

import com.example.kservicelib.Request;
import com.example.kservicelib.Response;

interface IKService {
    Response fibonacci(in Request request);
}