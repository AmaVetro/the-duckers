import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { Footer } from '../Footer';

describe('Footer', () => {
  it('muestra el texto de copyright', () => {
    render(<Footer />);
    expect(screen.getByText(/© 2025 The Duckers/i)).toBeInTheDocument();
  });

  it('se renderiza como un footer accesible', () => {
    render(<Footer />);
    expect(screen.getByRole('contentinfo')).toBeInTheDocument();
  });
});
