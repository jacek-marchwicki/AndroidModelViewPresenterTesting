package com.example.sample;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;

import static com.google.common.truth.Truth.assert_;

public class DaggerExampleTest {
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ObjectGraph.create(new Module()).inject(this);
    }

    @Mock   MyDatabase databaseMock;
    @Inject ClassToTest classToTest;

    @Test public void testWhenListIsNull_queryWillBeTrue()  {
        Mockito.when(databaseMock.query("* from t")).thenReturn(null);

        boolean check = classToTest.query("* from t");

        assert_().that(check).isTrue();
        Mockito.verify(databaseMock).query("* from t");
    }

    @dagger.Module(injects = DaggerExampleTest.class)
    class Module {
        @Provides MyDatabase provideMyDatabase() {
            return databaseMock;
        }
    }
}
