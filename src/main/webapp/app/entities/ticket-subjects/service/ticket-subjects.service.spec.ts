import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITicketSubjects } from '../ticket-subjects.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../ticket-subjects.test-samples';

import { TicketSubjectsService } from './ticket-subjects.service';

const requireRestSample: ITicketSubjects = {
  ...sampleWithRequiredData,
};

describe('TicketSubjects Service', () => {
  let service: TicketSubjectsService;
  let httpMock: HttpTestingController;
  let expectedResult: ITicketSubjects | ITicketSubjects[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TicketSubjectsService);
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

    it('should create a TicketSubjects', () => {
      const ticketSubjects = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ticketSubjects).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TicketSubjects', () => {
      const ticketSubjects = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ticketSubjects).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TicketSubjects', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TicketSubjects', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TicketSubjects', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a TicketSubjects', () => {
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

    describe('addTicketSubjectsToCollectionIfMissing', () => {
      it('should add a TicketSubjects to an empty array', () => {
        const ticketSubjects: ITicketSubjects = sampleWithRequiredData;
        expectedResult = service.addTicketSubjectsToCollectionIfMissing([], ticketSubjects);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ticketSubjects);
      });

      it('should not add a TicketSubjects to an array that contains it', () => {
        const ticketSubjects: ITicketSubjects = sampleWithRequiredData;
        const ticketSubjectsCollection: ITicketSubjects[] = [
          {
            ...ticketSubjects,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTicketSubjectsToCollectionIfMissing(ticketSubjectsCollection, ticketSubjects);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TicketSubjects to an array that doesn't contain it", () => {
        const ticketSubjects: ITicketSubjects = sampleWithRequiredData;
        const ticketSubjectsCollection: ITicketSubjects[] = [sampleWithPartialData];
        expectedResult = service.addTicketSubjectsToCollectionIfMissing(ticketSubjectsCollection, ticketSubjects);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketSubjects);
      });

      it('should add only unique TicketSubjects to an array', () => {
        const ticketSubjectsArray: ITicketSubjects[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ticketSubjectsCollection: ITicketSubjects[] = [sampleWithRequiredData];
        expectedResult = service.addTicketSubjectsToCollectionIfMissing(ticketSubjectsCollection, ...ticketSubjectsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ticketSubjects: ITicketSubjects = sampleWithRequiredData;
        const ticketSubjects2: ITicketSubjects = sampleWithPartialData;
        expectedResult = service.addTicketSubjectsToCollectionIfMissing([], ticketSubjects, ticketSubjects2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketSubjects);
        expect(expectedResult).toContain(ticketSubjects2);
      });

      it('should accept null and undefined values', () => {
        const ticketSubjects: ITicketSubjects = sampleWithRequiredData;
        expectedResult = service.addTicketSubjectsToCollectionIfMissing([], null, ticketSubjects, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ticketSubjects);
      });

      it('should return initial array if no TicketSubjects is added', () => {
        const ticketSubjectsCollection: ITicketSubjects[] = [sampleWithRequiredData];
        expectedResult = service.addTicketSubjectsToCollectionIfMissing(ticketSubjectsCollection, undefined, null);
        expect(expectedResult).toEqual(ticketSubjectsCollection);
      });
    });

    describe('compareTicketSubjects', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTicketSubjects(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTicketSubjects(entity1, entity2);
        const compareResult2 = service.compareTicketSubjects(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTicketSubjects(entity1, entity2);
        const compareResult2 = service.compareTicketSubjects(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTicketSubjects(entity1, entity2);
        const compareResult2 = service.compareTicketSubjects(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
