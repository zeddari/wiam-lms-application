import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISessionProvider } from '../session-provider.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../session-provider.test-samples';

import { SessionProviderService } from './session-provider.service';

const requireRestSample: ISessionProvider = {
  ...sampleWithRequiredData,
};

describe('SessionProvider Service', () => {
  let service: SessionProviderService;
  let httpMock: HttpTestingController;
  let expectedResult: ISessionProvider | ISessionProvider[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SessionProviderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SessionProvider', () => {
      const sessionProvider = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sessionProvider).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SessionProvider', () => {
      const sessionProvider = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sessionProvider).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SessionProvider', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SessionProvider', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SessionProvider', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SessionProvider', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addSessionProviderToCollectionIfMissing', () => {
      it('should add a SessionProvider to an empty array', () => {
        const sessionProvider: ISessionProvider = sampleWithRequiredData;
        expectedResult = service.addSessionProviderToCollectionIfMissing([], sessionProvider);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionProvider);
      });

      it('should not add a SessionProvider to an array that contains it', () => {
        const sessionProvider: ISessionProvider = sampleWithRequiredData;
        const sessionProviderCollection: ISessionProvider[] = [
          {
            ...sessionProvider,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSessionProviderToCollectionIfMissing(sessionProviderCollection, sessionProvider);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SessionProvider to an array that doesn't contain it", () => {
        const sessionProvider: ISessionProvider = sampleWithRequiredData;
        const sessionProviderCollection: ISessionProvider[] = [sampleWithPartialData];
        expectedResult = service.addSessionProviderToCollectionIfMissing(sessionProviderCollection, sessionProvider);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionProvider);
      });

      it('should add only unique SessionProvider to an array', () => {
        const sessionProviderArray: ISessionProvider[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sessionProviderCollection: ISessionProvider[] = [sampleWithRequiredData];
        expectedResult = service.addSessionProviderToCollectionIfMissing(sessionProviderCollection, ...sessionProviderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sessionProvider: ISessionProvider = sampleWithRequiredData;
        const sessionProvider2: ISessionProvider = sampleWithPartialData;
        expectedResult = service.addSessionProviderToCollectionIfMissing([], sessionProvider, sessionProvider2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sessionProvider);
        expect(expectedResult).toContain(sessionProvider2);
      });

      it('should accept null and undefined values', () => {
        const sessionProvider: ISessionProvider = sampleWithRequiredData;
        expectedResult = service.addSessionProviderToCollectionIfMissing([], null, sessionProvider, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sessionProvider);
      });

      it('should return initial array if no SessionProvider is added', () => {
        const sessionProviderCollection: ISessionProvider[] = [sampleWithRequiredData];
        expectedResult = service.addSessionProviderToCollectionIfMissing(sessionProviderCollection, undefined, null);
        expect(expectedResult).toEqual(sessionProviderCollection);
      });
    });

    describe('compareSessionProvider', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSessionProvider(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSessionProvider(entity1, entity2);
        const compareResult2 = service.compareSessionProvider(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSessionProvider(entity1, entity2);
        const compareResult2 = service.compareSessionProvider(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSessionProvider(entity1, entity2);
        const compareResult2 = service.compareSessionProvider(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
