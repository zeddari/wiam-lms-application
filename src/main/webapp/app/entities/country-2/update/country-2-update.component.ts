import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICountry2 } from '../country-2.model';
import { Country2Service } from '../service/country-2.service';
import { Country2FormService, Country2FormGroup } from './country-2-form.service';

@Component({
  standalone: true,
  selector: 'jhi-country-2-update',
  templateUrl: './country-2-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class Country2UpdateComponent implements OnInit {
  isSaving = false;
  country2: ICountry2 | null = null;

  editForm: Country2FormGroup = this.country2FormService.createCountry2FormGroup();

  constructor(
    protected country2Service: Country2Service,
    protected country2FormService: Country2FormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ country2 }) => {
      this.country2 = country2;
      if (country2) {
        this.updateForm(country2);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const country2 = this.country2FormService.getCountry2(this.editForm);
    if (country2.id !== null) {
      this.subscribeToSaveResponse(this.country2Service.update(country2));
    } else {
      this.subscribeToSaveResponse(this.country2Service.create(country2));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICountry2>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(country2: ICountry2): void {
    this.country2 = country2;
    this.country2FormService.resetForm(this.editForm, country2);
  }
}
