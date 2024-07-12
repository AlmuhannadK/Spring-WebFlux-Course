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

    @Test
    void namesFluxFlatMap() {

        // testing for dorothy name just to see if it will pass and test in one element
        var namesFluxFlatMap = fluxAndMonoGeneratorService.namesFluxFlatMap(5);
        StepVerifier.create(namesFluxFlatMap)
                .expectNext("D","O","R","O","T","H","Y")
                .verifyComplete();
        // passed unit test
    }

    @Test
    void namesFluxFlatMapAsync() {
        // assuming i will receive the list of elements in order
        // but it won't be ordered! IT SHOULD FAIL
        // "aaa", "bb", "cccc"
        var namesFluxFlatMapAsync = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(1);
        StepVerifier.create(namesFluxFlatMapAsync)
                //.expectNext("A","A","A", "B","B","C","C","C","C") //this will fail (no order)
                .expectNextCount(9) // since we give only expected num of elements, it will pass
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {

        // similar to flatMap but preserves the order of stream elements
        var namesFluxConcatMap = fluxAndMonoGeneratorService.namesFluxConcatMap(2);
        // replace e with X and split names
        StepVerifier.create(namesFluxConcatMap)
                .expectNext("a","l","X", "x", "b", "X", "n", "c","a", "X", "n")
                .verifyComplete();
        /*("alex", "ben", "caen", "dorothy")*/
    }
}
