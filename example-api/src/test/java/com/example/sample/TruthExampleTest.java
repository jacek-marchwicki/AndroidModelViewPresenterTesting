package com.example.sample;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assert_;

public class TruthExampleTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    MyDatabase databaseMock;

    @Test
    public void testWhenListIsNull_queryWillBeTrue()  {
        final ClassToTest t  = new ClassToTest(databaseMock);
        Mockito.when(databaseMock.query("* from t")).thenReturn(null);

        final boolean check = t.query("* from t");

        assert_().that(check).isTrue();
        Mockito.verify(databaseMock).query("* from t");
    }

}
