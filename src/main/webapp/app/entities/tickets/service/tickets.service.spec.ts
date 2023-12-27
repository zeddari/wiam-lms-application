import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITickets } from '../tickets.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tickets.test-samples';

import { TicketsService, RestTickets } from './tickets.service';

const requireRestSample: RestTickets = {
  ...sampleWithRequiredData,
  dateTicket: sampleWithRequiredData.dateTicket?.toJSON(),
  dateProcess: sampleWithRequiredData.dateProcess?.toJSON(),
};

describe('Tickets Service', () => {
  let service: TicketsService;
  let httpMock: HttpTestingController;
  let expectedResult: ITickets | ITickets[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TicketsService);
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

    it('should create a Tickets', () => {
      const tickets = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tickets).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tickets', () => {
      const tickets = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tickets).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tickets', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tickets', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Tickets', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Tickets', () => {
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

    describe('addTicketsToCollectionIfMissing', () => {
      it('should add a Tickets to an empty array', () => {
        const tickets: ITickets = sampleWithRequiredData;
        expectedResult = service.addTicketsToCollectionIfMissing([], tickets);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tickets);
      });

      it('should not add a Tickets to an array that contains it', () => {
        const tickets: ITickets = sampleWithRequiredData;
        const ticketsCollection: ITickets[] = [
          {
            ...tickets,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTicketsToCollectionIfMissing(ticketsCollection, tickets);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tickets to an array that doesn't contain it", () => {
        const tickets: ITickets = sampleWithRequiredData;
        const ticketsCollection: ITickets[] = [sampleWithPartialData];
        expectedResult = service.addTicketsToCollectionIfMissing(ticketsCollection, tickets);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tickets);
      });

      it('should add only unique Tickets to an array', () => {
        const ticketsArray: ITickets[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ticketsCollection: ITickets[] = [sampleWithRequiredData];
        expectedResult = service.addTicketsToCollectionIfMissing(ticketsCollection, ...ticketsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tickets: ITickets = sampleWithRequiredData;
        const tickets2: ITickets = sampleWithPartialData;
        expectedResult = service.addTicketsToCollectionIfMissing([], tickets, tickets2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tickets);
        expect(expectedResult).toContain(tickets2);
      });

      it('should accept null and undefined values', () => {
        const tickets: ITickets = sampleWithRequiredData;
        expectedResult = service.addTicketsToCollectionIfMissing([], null, tickets, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tickets);
      });

      it('should return initial array if no Tickets is added', () => {
        const ticketsCollection: ITickets[] = [sampleWithRequiredData];
        expectedResult = service.addTicketsToCollectionIfMissing(ticketsCollection, undefined, null);
        expect(expectedResult).toEqual(ticketsCollection);
      });
    });

    describe('compareTickets', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTickets(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTickets(entity1, entity2);
        const compareResult2 = service.compareTickets(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTickets(entity1, entity2);
        const compareResult2 = service.compareTickets(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTickets(entity1, entity2);
        const compareResult2 = service.compareTickets(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
