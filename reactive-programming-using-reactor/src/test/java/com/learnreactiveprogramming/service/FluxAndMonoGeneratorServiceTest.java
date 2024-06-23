package com.learnreactiveprogramming.service;


import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {


    // REACTOR UNIT TESTING

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();


    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then (reactor test)
        StepVerifier.create(namesFlux)
                //.expectNext("Ahmad", "Bader", "Caeser")
                //.expectNextCount(3)
                .expectNext("Ahmad")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFluxMap() {

        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxMap();

        StepVerifier.create(namesFluxMap)
                .expectNext("IBRAHIM", "MOHAMMAD", "ISMAEL", "MOUSA")
                //.expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesFluxImmutable() {

        var namesFluxImmutable = fluxAndMonoGeneratorService.namesFluxImmutable();

        StepVerifier.create(namesFluxImmutable)
                .expectNext("aaa", "bbb", "ccc")
                //.expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void namesFluxFilter() {

        var namesFluxFilter = fluxAndMonoGeneratorService.namesFluxFilter(3);

        StepVerifier.create(namesFluxFilter)
                .expectNext("4-eddy", "6-johnny", "4-jack", "7-jameson")
                .verifyComplete();
    }
}
